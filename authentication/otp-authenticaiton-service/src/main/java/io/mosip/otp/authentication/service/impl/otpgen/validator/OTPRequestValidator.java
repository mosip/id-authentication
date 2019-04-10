package io.mosip.otp.authentication.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.id.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.NotificationType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
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

	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "otprequest.received-time-allowed.in-minutes";

	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	private static final String OTP_VALIDATOR = "OTP_VALIDATOR";

	private static final String SESSION_ID = "session_id";

	private static final String REQ_TIME = "requestTime";

	private static final String INDIVIDUAL_ID = "individualId";

	private static final String TRANSACTION_ID = "transactionID";

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

			validateReqTime(otpRequestDto.getRequestTime(), errors, REQ_TIME);

			validateTxnId(otpRequestDto.getTransactionID(), errors, TRANSACTION_ID);

			if (!errors.hasErrors()) {
				validateRequestTimedOut(otpRequestDto.getRequestTime(), errors);
			}

			if (!errors.hasErrors()) {
				validateId(otpRequestDto.getId(), errors);

				validateIdvId(otpRequestDto.getIndividualId(), otpRequestDto.getIndividualIdType(), errors,
						INDIVIDUAL_ID);
			}

			if (!errors.hasErrors()) {
				validateOtpChannel(otpRequestDto.getOtpChannel(), errors);
			}

		}
		// validateVer(otpRequestDto.getVer(), errors);
	}

	private void validateOtpChannel(List<String> otpChannel, Errors errors) {
		if (otpChannel != null && !otpChannel.isEmpty()) {
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

			String maxTimeInMinutes = env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS);
			Instant reqTimeInstance = DateUtils.parseToDate(timestamp, env.getProperty(DATETIME_PATTERN)).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			if (maxTimeInMinutes != null
					&& Duration.between(reqTimeInstance, now).toMinutes() > Integer.parseInt(maxTimeInMinutes)) {
				mosipLogger.debug(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_OTP_REQUEST_TIMESTAMP -- " + String.format(
								IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes() - Long.parseLong(maxTimeInMinutes)));
				errors.rejectValue(REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"INVALID_INPUT_PARAMETER -- " + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
