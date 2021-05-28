package io.mosip.authentication.common.service.integration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.OtpGenerateRequestDto;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.indauth.dto.SenderType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.util.MaskUtil;
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

	/** The Constant NAME. */
	private static final String NAME = "name";
	/** The Constant TIME. */
	private static final String TIME = "time";
	/** The Constant DATE. */
	private static final String DATE = "date";

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
	public boolean sendOtp(OtpRequestDTO otpRequestDTO, String idvid, String idvidType, Map<String, String> valueMap)
			throws IdAuthenticationBusinessException {

		Map<String, Object> otpTemplateValues = getOtpTemplateValues(otpRequestDTO, idvid, idvidType, valueMap);
		String otp = generateOTP(otpRequestDTO.getIndividualId());
		otpTemplateValues.put("otp", otp);
		String otpHash;
		otpHash = IdAuthSecurityManager.digestAsPlainText((otpRequestDTO.getIndividualId().toString()
				+ environment.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER) + otpRequestDTO.getTransactionID()
				+ environment.getProperty(IdAuthConfigKeyConstants.KEY_SPLITTER) + otp).getBytes());

		if (otpRepo.existsByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS)) {
			OtpTransaction otpTxn = otpRepo.findByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS);
			otpTxn.setOtpHash(otpHash);
			otpTxn.setUpdBy(securityManager.getUser());
			otpTxn.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			otpTxn.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plusSeconds(
					environment.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME, Long.class)));
			otpTxn.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
			otpRepo.save(otpTxn);
		} else {
			OtpTransaction txn = new OtpTransaction();
			txn.setId(UUID.randomUUID().toString());
			txn.setRefId(securityManager.hash(otpRequestDTO.getIndividualId()));
			txn.setOtpHash(otpHash);
			txn.setCrBy(securityManager.getUser());
			txn.setCrDtimes(DateUtils.getUTCCurrentDateTime());
			txn.setExpiryDtimes(DateUtils.getUTCCurrentDateTime().plusSeconds(
					environment.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME, Long.class)));
			txn.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
			otpRepo.save(txn);
		}
		String notificationProperty = null;
		notificationProperty = otpRequestDTO
				.getOtpChannel().stream().map(channel -> NotificationType.getNotificationTypeForChannel(channel)
						.stream().map(type -> type.getName()).collect(Collectors.joining()))
				.collect(Collectors.joining("|"));

		notificationService.sendNotification(otpTemplateValues, valueMap.get(IdAuthCommonConstants.EMAIL),
				valueMap.get(IdAuthCommonConstants.PHONE_NUMBER), SenderType.OTP, notificationProperty);

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

	/*
	 * Send Otp Notification
	 * 
	 */
	private Map<String, Object> getOtpTemplateValues(OtpRequestDTO otpRequestDto, String idvid, String idvidType,
			Map<String, String> valueMap) {

		Entry<String, String> dateAndTime = getDateAndTime(otpRequestDto.getRequestTime(),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String date = dateAndTime.getKey();
		String time = dateAndTime.getValue();

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		String charCount = environment.getProperty(IdAuthConfigKeyConstants.UIN_MASKING_CHARCOUNT);
		if (charCount != null) {
			maskedUin = MaskUtil.generateMaskValue(idvid, Integer.parseInt(charCount));
		}
		values.put("idvid", maskedUin);
		values.put("idvidType", idvidType);
		Integer timeInSeconds = environment.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME,
				Integer.class);
		int timeInMinutes = (timeInSeconds % 3600) / 60;
		values.put("validTime", String.valueOf(timeInMinutes));
		values.put(DATE, date);
		values.put(TIME, time);
		values.put(NAME, valueMap.get(IdAuthCommonConstants.NAME_PRI));
		values.put(NAME + "_" + valueMap.get(IdAuthCommonConstants.PRIMARY_LANG),
				valueMap.get(IdAuthCommonConstants.NAME_PRI));
		values.put(NAME + "_" + valueMap.get(IdAuthCommonConstants.SECONDAY_LANG),
				valueMap.get(IdAuthCommonConstants.NAME_SEC));
		return values;
	}

	/**
	 * Gets the date and time.
	 *
	 * @param requestTime the request time
	 * @param pattern     the pattern
	 * @return the date and time
	 */
	private static Entry<String, String> getDateAndTime(String requestTime, String pattern) {

		String[] dateAndTime = new String[2];

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(pattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(requestTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime dateTime3 = ZonedDateTime.now(zone);
		ZonedDateTime dateTime = dateTime3.withZoneSameInstant(zone);
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dateAndTime[0] = date;
		String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		dateAndTime[1] = time;

		return new SimpleEntry<>(date, time);

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
		if (otpRepo.existsByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS)) {
			OtpTransaction otpTxn = otpRepo.findByOtpHashAndStatusCode(otpHash, IdAuthCommonConstants.ACTIVE_STATUS);
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
