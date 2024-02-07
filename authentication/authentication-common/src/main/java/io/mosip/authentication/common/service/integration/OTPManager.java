package io.mosip.authentication.common.service.integration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OtpGenerateRequestDto;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private OtpTxnRepository otpRepo;

	@Autowired
	private NotificationService notificationService;
	
	@Value("${mosip.ida.otp.validation.attempt.count.threshold:5}")
	private int numberOfValidationAttemptsAllowed;
	
	@Value("${mosip.ida.otp.frozen.duration.minutes:30}")
	private int otpFrozenTimeMinutes;

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
		
		String refIdHash = securityManager.hash(idvid);
		Optional<OtpTransaction> otpEntityOpt = otpRepo.findFirstByRefIdOrderByGeneratedDtimesDesc(refIdHash);

		if(otpEntityOpt.isPresent()) {
			OtpTransaction otpEntity = otpEntityOpt.get();
			if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.FROZEN)) {
				if(isAfterFrozenDuration(otpEntity)) {
					logger.info("OTP Frozen wait time is over. Allowing further.");
					otpEntity.setValidationRetryCount(0);
					otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
				} else {
					throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorMessage(),
									otpFrozenTimeMinutes + "seconds", numberOfValidationAttemptsAllowed));
				}
			}
		}
		
		String otp = generateOTP(otpRequestDTO.getIndividualId());
		LocalDateTime otpGenerationTime = DateUtils.getUTCCurrentDateTime();
		String otpHash = IdAuthSecurityManager.digestAsPlainText((otpRequestDTO.getIndividualId()
				+ EnvUtil.getKeySplitter() + otpRequestDTO.getTransactionID()
				+ EnvUtil.getKeySplitter() + otp).getBytes());
		
		if (otpEntityOpt.isPresent()) {
			OtpTransaction otpTxn = otpEntityOpt.get();
			otpTxn.setOtpHash(otpHash);
			otpTxn.setUpdBy(securityManager.getUser());
			otpTxn.setUpdDTimes(otpGenerationTime);
			otpTxn.setExpiryDtimes(otpGenerationTime.plusSeconds(EnvUtil.getOtpExpiryTime()));
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
					EnvUtil.getOtpExpiryTime()));
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
			if ( response!=null && response.getResponse()!=null && response.getResponse().get("status")!=null && response.getResponse().get("status").equals(USER_BLOCKED)) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), USER_BLOCKED);
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
			}
			if(response !=null && response.getResponse()!=null){
				return response.getResponse().get("otp");
			} else{
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			}

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
	public boolean validateOtp(String pinValue, String otpKey, String individualId) throws IdAuthenticationBusinessException {
		String refIdHash = securityManager.hash(individualId);
		Optional<OtpTransaction> otpEntityOpt = otpRepo.findFirstByRefIdOrderByGeneratedDtimesDesc(refIdHash);

		requireKeyNotFound(otpEntityOpt);
		
		OtpTransaction otpEntity = otpEntityOpt.get();
		
		if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.FROZEN)) {
			if(otpEntity.getUpdDTimes() != null && isAfterFrozenDuration(otpEntity)) {
				logger.info("OTP Frozen wait time is over. Allowing further.");
				otpEntity.setValidationRetryCount(0);
				otpEntity.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(), String.format(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorMessage(), otpFrozenTimeMinutes + "seconds"));
			}
		}
		
		// Increment the validation attempt count.
		int attemptCount = otpEntity.getValidationRetryCount() == null ? 1 : otpEntity.getValidationRetryCount() + 1;
		
		if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.ACTIVE_STATUS)) {
			String otpHash = getOtpHash(pinValue, otpKey);
			if (otpEntity.getOtpHash().equals(otpHash)) {
				otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
				otpEntity.setStatusCode(IdAuthCommonConstants.USED_STATUS);
				otpRepo.save(otpEntity);
				if (otpEntity.getExpiryDtimes().isAfter(DateUtils.getUTCCurrentDateTime())) {
					return true;
				} else {
					logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), OTP_EXPIRED);
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.EXPIRED_OTP);
				}
			} else {
				//Set the incremented validation attempt count
				otpEntity.setValidationRetryCount(attemptCount);
				if (attemptCount >= numberOfValidationAttemptsAllowed) {
					otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
				}
				otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
				otpRepo.save(otpEntity);
				return false;
			}
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_VAL_KEY_NOT_FOUND);
		}
	}

	private boolean isAfterFrozenDuration(OtpTransaction otpEntity) {
		return DateUtils.getUTCCurrentDateTime().isAfter(otpEntity.getUpdDTimes().plus(otpFrozenTimeMinutes, ChronoUnit.MINUTES));
	}

	private String getOtpHash(String pinValue, String otpKey) {
		return IdAuthSecurityManager.digestAsPlainText(
				(otpKey + EnvUtil.getKeySplitter() + pinValue).getBytes());
	}
	
	private void requireKeyNotFound(Optional<OtpTransaction> entityOpt) throws IdAuthenticationBusinessException {
		/*
		 * Checking whether the key exists in repository or not. If not, throw an
		 * exception.
		 */
		if (entityOpt.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_VAL_KEY_NOT_FOUND);
		}
	}
}
