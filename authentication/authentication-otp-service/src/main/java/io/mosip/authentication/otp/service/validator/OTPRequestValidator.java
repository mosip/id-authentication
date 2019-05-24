package io.mosip.authentication.otp.service.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator extends IdAuthValidator {

	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	private static final String OTP_VALIDATOR = "OTP_VALIDATOR";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPRequestValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		if (Objects.nonNull(target)) {
			OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

			validateReqTime(otpRequestDto.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);

			validateTxnId(otpRequestDto.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);

			if (!errors.hasErrors()) {
				validateRequestTimedOut(otpRequestDto.getRequestTime(), errors);
			}

			if (!errors.hasErrors()) {
				validateId(otpRequestDto.getId(), errors);

				validateIdvId(otpRequestDto.getIndividualId(), otpRequestDto.getIndividualIdType(), errors,
						IdAuthCommonConstants.IDV_ID);
			}

			if (!errors.hasErrors()) {
				validateOtpChannel(otpRequestDto.getOtpChannel(), errors);
			}

		}
		// validateVer(otpRequestDto.getVer(), errors);
	}

	private void validateOtpChannel(List<String> otpChannel, Errors errors) {
		if (otpChannel == null || otpChannel.isEmpty() || otpChannel.get(0).isEmpty()) {
			errors.reject(IdAuthenticationErrorConstants.OTP_CHANNEL_NOT_PROVIDED.getErrorCode(),
					IdAuthenticationErrorConstants.OTP_CHANNEL_NOT_PROVIDED.getErrorMessage());
		} else {
			String channels = otpChannel.stream()
					.filter(channel -> !NotificationType.getNotificationTypeForChannel(channel).isPresent())
					.collect(Collectors.joining(","));
			if (!channels.isEmpty()) {
				errors.reject(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"otpChannel - " + channels));
			}
		}
	}

	/**
	 * Checks if is timestamp valid.
	 *
	 * @param timestamp the timestamp
	 * @return true, if is timestamp valid
	 */
	private void validateRequestTimedOut(String timestamp, Errors errors) {
		try {

			String maxTimeInMinutes = env
					.getProperty(IdAuthConfigKeyConstants.AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES);
			Instant reqTimeInstance = DateUtils
					.parseToDate(timestamp, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			if (maxTimeInMinutes != null
					&& Duration.between(reqTimeInstance, now).toMinutes() > Integer.parseInt(maxTimeInMinutes)) {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_OTP_REQUEST_TIMESTAMP -- " + String.format(
								IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes() - Long.parseLong(maxTimeInMinutes)));
				errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode(),
						new Object[] { maxTimeInMinutes },
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"INVALID_INPUT_PARAMETER -- " + IdAuthCommonConstants.REQ_TIME);
			errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IdAuthCommonConstants.REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
