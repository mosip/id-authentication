package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.OtpErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.MaskUtil;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * OTPManager handling with OTP-Generation and OTP-Validation.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 */
@Component
public class OTPManager {

	/** The Constant NAME. */
	private static final String NAME = "name";
	/** The Constant TIME. */
	private static final String TIME = "time";
	/** The Constant DATE. */
	private static final String DATE = "date";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant VALIDATION_UNSUCCESSFUL. */
	private static final String VALIDATION_UNSUCCESSFUL = "VALIDATION_UNSUCCESSFUL";

	/** The Constant OTP_EXPIRED. */
	private static final String OTP_EXPIRED = "OTP_EXPIRED";

	/** The Constant STATUS_SUCCESS. */
	private static final String STATUS_SUCCESS = "success";

	/** The Constant STATUS_FAILURE. */
	private static final String STATUS_FAILURE = "failure";
	
	/** The Constant Message. */
	private static final String MESSAGE = "message";

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

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(OTPManager.class);

	/**
	 * Generate OTP with information of {@link MediaType } and OTP generation
	 * time-out.
	 *
	 * @param otpRequestDTO the otp request DTO
	 * @param uin the uin
	 * @param valueMap the value map
	 * @return String(otp)
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	public boolean sendOtp(OtpRequestDTO otpRequestDTO, String userIdForSendOtp, String userIdTypeForSendOtp, Map<String, String> valueMap)
			throws IdAuthenticationBusinessException {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		RestRequestDTO restRequestDTO = null;
		String response = null;
		String message = null;
		boolean isOtpGenerated = false;
		try {
			String appId = environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID);
			String context = environment.getProperty(IdAuthConfigKeyConstants.OTP_CONTEXT);
			otpGeneratorRequestDto.setAppId(appId);
			otpGeneratorRequestDto.setContext(context);
			otpGeneratorRequestDto.setUserId(userIdForSendOtp);
			Map<String, Object> otpTemplateValues = getOtpTemplateValues(otpRequestDTO, userIdForSendOtp, valueMap);
			otpGeneratorRequestDto.setTemplateVariables(otpTemplateValues);
			otpGeneratorRequestDto.setUseridtype(userIdTypeForSendOtp);
			List<String> otpChannel = new ArrayList<>();
			for (String channel : otpRequestDTO.getOtpChannel()) {
				NotificationType.getNotificationTypeForChannel(channel).ifPresent(type -> otpChannel.add(type.getApiChannel().toLowerCase()));
			}
			otpGeneratorRequestDto.setOtpChannel(otpChannel);
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
					RestRequestFactory.createRequest(otpGeneratorRequestDto), ResponseWrapper.class);
			ResponseWrapper<OtpGeneratorResponseDto> otpGeneratorResponsetDto = restHelper.requestSync(restRequestDTO);
			response = (String) ((Map<String, Object>) otpGeneratorResponsetDto.getResponse()).get(STATUS);
			message = (String) ((Map<String, Object>) otpGeneratorResponsetDto.getResponse()).get(MESSAGE);
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
					"otpGeneratorResponsetDto " + response);
			if (response != null) {
				if (response.equalsIgnoreCase(STATUS_SUCCESS)) {
					isOtpGenerated = true;
				} else if (response.equalsIgnoreCase(STATUS_FAILURE) && message.equalsIgnoreCase(USER_BLOCKED)) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BLOCKED_OTP_GENERATE);
				}
			}
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				handleOtpErrorResponse(responseBody.get());

			} else {
				// FIXME Could not validate OTP -OTP - Request could not be processed. Please
				// try again
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
			}

		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, e);
		}
		return isOtpGenerated;
	}

	/**
	 * Handle otp error response.
	 *
	 * @param responseBody the response body
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	private void handleOtpErrorResponse(Object responseBody) throws IdAuthenticationBusinessException {
		ResponseWrapper<OtpGeneratorResponseDto> otpGeneratorResponsetDto = (ResponseWrapper<OtpGeneratorResponseDto>) responseBody;
		List<ServiceError> errorList = otpGeneratorResponsetDto.getErrors();
		if (errorList != null && !errorList.isEmpty()) {
			if (errorList.stream().anyMatch(errors -> errors.getErrorCode()
					.equalsIgnoreCase(OtpErrorConstants.PHONENOTREGISTERED.getErrorCode()))) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage(),
								IdaIdMapping.PHONE.name()));
			} else if (errorList.stream().anyMatch(errors -> errors.getErrorCode()
					.equalsIgnoreCase(OtpErrorConstants.EMAILNOTREGISTERED.getErrorCode()))) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage(),
								IdaIdMapping.EMAIL.name()));
			} else if (errorList.stream().anyMatch(errors -> errors.getErrorCode()
					.equalsIgnoreCase(OtpErrorConstants.EMAILPHONENOTREGISTERED.getErrorCode()))) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage(),
								IdaIdMapping.EMAIL.name() + "," + IdaIdMapping.PHONE.name()));
			}
		}
	}

	/*
	 * Send Otp Notification
	 * 
	 */
	private Map<String, Object> getOtpTemplateValues(OtpRequestDTO otpRequestDto, String uin,
			Map<String, String> valueMap) {

		Entry<String, String> dateAndTime = getDateAndTime(otpRequestDto.getRequestTime(),
				environment.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String date = dateAndTime.getKey();
		String time = dateAndTime.getValue();

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		String charCount = environment.getProperty(IdAuthConfigKeyConstants.UIN_MASKING_CHARCOUNT);
		if (charCount != null) {
			maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(charCount));
		}
		values.put("uin", maskedUin);
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
	@SuppressWarnings("unchecked")
	public boolean validateOtp(String pinValue, String otpKey) throws IdAuthenticationBusinessException {
		boolean isValidOtp = false;
		try {
			RestRequestDTO restreqdto = restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE,
					null, Map.class);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("key", otpKey);
			params.add("otp", pinValue);
			restreqdto.setParams(params);
			Map<String, Object> otpvalidateresponsedto = restHelper.requestSync(restreqdto);
			isValidOtp = Optional.ofNullable((Map<String, Object>) otpvalidateresponsedto.get(RESPONSE))
					.filter(res -> res.containsKey(STATUS)).map(res -> String.valueOf(res.get(STATUS)))
					.filter(status -> status.equalsIgnoreCase(STATUS_SUCCESS)).isPresent();
			if (!isValidOtp) {
				handleErrorStatus(null, otpvalidateresponsedto);
			}
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					e.getErrorCode() + e.getErrorText(), e.getResponseBodyAsString().orElse(""));

			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> res = (Map<String, Object>) responseBody.get();
				handleErrorStatus(e, res);
			}
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "Inside validateOtp", null);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return isValidOtp;
	}

	@SuppressWarnings("unchecked")
	private void handleErrorStatus(RestServiceException e, Map<String, Object> res)
			throws IdAuthenticationBusinessException {
		Object status = res.get(RESPONSE) instanceof Map ? ((Map<String, Object>) res.get(RESPONSE)).get(STATUS) : null;
		Object message = res.get(RESPONSE) instanceof Map ? ((Map<String, Object>) res.get(RESPONSE)).get(MESSAGE)
				: null;
		if (status instanceof String && message instanceof String) {
			if (((String) status).equalsIgnoreCase(STATUS_FAILURE)) {
				throwOtpException((String) message);
			} else {
				throwKeyNotFound(e);
			}
		} else if (e != null) {
			throwKeyNotFound(e);
		}
	}

	/**
	 * Throws KeyNotFound Exception when otp key is Invalid
	 * 
	 * @param e
	 * @throws IdAuthenticationBusinessException
	 */
	private void throwKeyNotFound(RestServiceException e) throws IdAuthenticationBusinessException {
		Optional<String> errorCode = e.getResponseBodyAsString().flatMap(this::getErrorCode);
		// Do not throw server error for OTP not generated, throw invalid OTP error
		// instead
		logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
				e.getErrorText());
		if (errorCode.filter(code -> code.equalsIgnoreCase(IdAuthCommonConstants.KER_OTP_KEY_NOT_EXISTS_CODE))
				.isPresent()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		}
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
	}

	private void throwOtpException(String message) throws IdAuthenticationBusinessException {
		if (message.equalsIgnoreCase(USER_BLOCKED)) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), USER_BLOCKED);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
		} else if (message.equalsIgnoreCase(OTP_EXPIRED)) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), OTP_EXPIRED);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.EXPIRED_OTP);
		} else if (message.equalsIgnoreCase(VALIDATION_UNSUCCESSFUL)) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(), VALIDATION_UNSUCCESSFUL);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		} else {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthenticationErrorConstants.SERVER_ERROR.getErrorCode(),
					IdAuthenticationErrorConstants.SERVER_ERROR.getErrorMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		}
	}

	/**
	 * Gets the error code.
	 *
	 * @param resBody the res body
	 * @return the error code
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Optional<String> getErrorCode(String resBody) {
		return Optional.of(resBody).map(str -> {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> res = null;
			try {
				res = mapper.readValue(str, Map.class);
			} catch (IOException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						"Error parsing response body", null);
			}
			return res;
		}).map(map -> map.get("errors")).filter(obj -> obj instanceof List).flatMap(obj -> ((List) obj).stream()
				.filter(obj1 -> obj1 instanceof Map).map(map1 -> (((Map) map1).get("errorCode"))).findAny())
				.map(String::valueOf);
	}

}
