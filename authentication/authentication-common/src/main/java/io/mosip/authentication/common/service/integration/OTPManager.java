package io.mosip.authentication.common.service.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OtpGenerateRequestDto;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * OTPManager handling with OTP-Generation and OTP-Validation.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */
@Component
public class OTPManager {

	/** The Constant OTP_EXPIRED. */
	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	/** The Constant USER_BLOCKED. */
	private static final String USER_BLOCKED = "USER_BLOCKED";

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	@Autowired
	private Environment environment;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private OtpTxnRepository otpRepo;

	@Autowired
	private NotificationService notificationService;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/**
	 * Generate OTP with information of {@link MediaType } and OTP generation
	 * time-out.
	 *
	 * @param otpRequestDTO the otp request DTO
	 * @param uin           the uin
	 * @param valueMap      the value map
	 * @return String(otp)
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public boolean sendOtp(OtpRequestDTO otpRequestDTO, String idvid, String idvidType, Map<String, String> valueMap, List<String> templateLanguages)
			throws IdAuthenticationBusinessException {

		String otp = generateOTP(otpRequestDTO.getIndividualId());
		LocalDateTime otpGenerationTime = DateUtils.getUTCCurrentDateTime();
		String otpHash = IdAuthSecurityManager.digestAsPlainText((otpRequestDTO.getIndividualId()
				+ environment.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER) + otpRequestDTO.getTransactionID()
				+ environment.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER) + otp).getBytes());

		Optional<OtpTransaction> otpTxnOpt = otpRepo.findByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS);
		if (otpTxnOpt.isPresent()) {
			OtpTransaction otpTxn = otpTxnOpt.get();
			otpTxn.setOtpHash(otpHash);
			otpTxn.setUpdBy(securityManager.getUser());
			otpTxn.setUpdDTimes(otpGenerationTime);
			otpTxn.setExpiryDtimes(otpGenerationTime.plusSeconds(
					environment.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME, Long.class)));
			otpTxn.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
			otpRepo.save(otpTxn);
		} else {
			OtpTransaction txn = new OtpTransaction();
			txn.setId(UUID.randomUUID().toString());
			txn.setRefId(securityManager.hash(otpRequestDTO.getIndividualId()));
			txn.setOtpHash(otpHash);
			txn.setCrBy(securityManager.getUser());
			txn.setCrDtimes(otpGenerationTime);
			txn.setExpiryDtimes(otpGenerationTime.plusSeconds(
					environment.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME, Long.class)));
			txn.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
			otpRepo.save(txn);
		}
		String notificationProperty = null;
		notificationProperty = otpRequestDTO
				.getOtpChannel().stream().map(channel -> NotificationType.getNotificationTypeForChannel(channel)
						.stream().map(NotificationType::getName).collect(Collectors.joining()))
				.collect(Collectors.joining("|"));

		notificationService.sendOTPNotification(idvid, idvidType, valueMap, templateLanguages, otp, notificationProperty, otpGenerationTime);

		return true;
	}

	private String generateOTP(String uin) throws IdAuthUncheckedException {
		try {
			OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(uin);
			RequestWrapper<OtpGenerateRequestDto> reqWrapper = new RequestWrapper<>();
			reqWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
			reqWrapper.setRequest(otpGenerateRequestDto);
			RestRequestDTO restRequest = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					reqWrapper, ResponseWrapper.class);
			ResponseWrapper<Map<String, String>> response = restHelper.requestSync(restRequest);
			if (response != null && response.getResponse().get("status").equals(USER_BLOCKED)) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), USER_BLOCKED);
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
			}
			return response.getResponse().get("otp");
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
					e.getMessage());
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthenticationErrorConstants.SERVER_ERROR.getErrorCode(),
					IdAuthenticationErrorConstants.SERVER_ERROR.getErrorMessage());
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}

	/**
	 * Validate method for OTP Validation.
	 *
	 * @param pinValue the pin value
	 * @param otpKey   the otp key
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public boolean validateOtp(String pinValue, String otpKey) throws IdAuthenticationBusinessException {
		String otpHash;
		otpHash = IdAuthSecurityManager.digestAsPlainText(
				(otpKey + environment.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER) + pinValue).getBytes());
		Optional<OtpTransaction> otpTxnOpt = otpRepo.findByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS);
		if (otpTxnOpt.isPresent()) {
			OtpTransaction otpTxn = otpTxnOpt.get();
			//OtpTransaction otpTxn = otpRepo.findByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS);
			otpTxn.setStatusCode(IdAuthCommonConstants.USED_STATUS);
			otpRepo.save(otpTxn);
			if (otpTxn.getExpiryDtimes().isAfter(DateUtils.getUTCCurrentDateTime())) {
				return true;
			} else {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), OTP_EXPIRED);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.EXPIRED_OTP);
			}
		} else {
			return false;
		}
	}
}
