package io.mosip.authentication.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator extends IdAuthValidator {

	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "otprequest.received-time-allowed.in-minutes";

	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	private static final String OTP_VALIDATOR = "OTP_VALIDATOR";

	private static final String SESSION_ID = "session_id";

	private static final String REQ_TIME = "reqTime";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPRequestValidator.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private DateHelper dateHelper;

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

		OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

		validateReqTime(otpRequestDto.getReqTime(), errors);

		if (!errors.hasErrors()) {
			validateRequestTimedOut(otpRequestDto.getReqTime(), errors);
		}

		if (!errors.hasErrors()) {
			validateId(otpRequestDto.getId(), errors);

			validateVer(otpRequestDto.getVer(), errors);

			validateIdvId(otpRequestDto.getIdvId(), otpRequestDto.getIdvIdType(), errors);

			validateMuaCode(otpRequestDto.getMuaCode(), errors);

			validateTxnId(otpRequestDto.getTxnID(), errors);
		}
	}

	/**
	 * Checks if is timestamp valid.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @return true, if is timestamp valid
	 */
	private void validateRequestTimedOut(String timestamp, Errors errors) {
		try {
			Instant reqTimeInstance = dateHelper.convertStringToDate(timestamp).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			if (Duration.between(reqTimeInstance, now).toMinutes() > env
					.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class)) {
				mosipLogger.debug(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_OTP_REQUEST_TIMESTAMP -- " + String.format(
								IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes()
										- env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class)));
				errors.rejectValue(REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
						new Object[] { Duration.between(reqTimeInstance, now).toMinutes() },
						IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | IDDataValidationException e) {
			mosipLogger.error(SESSION_ID, OTP_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"INVALID_INPUT_PARAMETER -- " + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
