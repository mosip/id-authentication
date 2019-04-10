package io.mosip.authentication.common.service.impl.indauth.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.auth.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Manoj SP
 * @author Rakesh Roshan
 * 
 */
@Component
public class AuthRequestValidator extends BaseAuthRequestValidator {

	private static final String REQUEST_TRANSACTION_ID = "request/transactionID";

	private static final String TRANSACTION_ID = "transactionID";

	private static final String REQUEST_REQUEST_TIME = "request/timestamp";

	private static final String REQUEST_TIME = "requestTime";

	private static final String ALLOWED_AUTH_TYPE = "auth.types.allowed";

	/** The Constant AUTH_REQUEST. */
	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "requestedAuth";

	/** The Constant MISSING_INPUT_PARAMETER. */
	private static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = REQUEST_TIME;

	/** The Constant VALIDATE_REQUEST_TIMED_OUT. */
	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	/** The Constant REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS. */
	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "authrequest.received-time-allowed.in-hours";

	/** The Constant INVALID_AUTH_REQUEST. */
	private static final String INVALID_AUTH_REQUEST = "INVALID_AUTH_REQUEST-No auth type found";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequestDto = (AuthRequestDTO) target;

		if (authRequestDto != null) {
			if (!errors.hasErrors()) {
				validateConsentReq(authRequestDto, errors);
				validateAllowedAuthTypes(authRequestDto, errors, ALLOWED_AUTH_TYPE);
			}
			if (!errors.hasErrors()) {
				validateReqTime(authRequestDto.getRequestTime(), errors, REQUEST_TIME);
				// Validation for Time Stamp in the RequestDTO.
				validateReqTime(authRequestDto.getRequest().getTimestamp(), errors, REQUEST_REQUEST_TIME);
			}
			if (!errors.hasErrors()) {
				validateTxnId(authRequestDto.getTransactionID(), errors, TRANSACTION_ID);
				// Validation for TransaactionId in the RequestDTO.
				validateTxnId(authRequestDto.getRequest().getTransactionID(), errors, REQUEST_TRANSACTION_ID);
			}
			if (!errors.hasErrors()) {
				validateAuthType(authRequestDto.getRequestedAuth(), errors);
			}
			if (!errors.hasErrors()) {
				validateRequestTimedOut(authRequestDto.getRequestTime(), errors);
			}

			if (!errors.hasErrors()) {
				super.validate(target, errors);
				String individualId = authRequestDto.getIndividualId();
				String individualIdType = authRequestDto.getIndividualIdType();

				validateIdvId(individualId, individualIdType, errors);

				if (!errors.hasErrors()) {
					checkAuthRequest(authRequestDto, errors);
				}
			}
		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/**
	 * Validate request timed out.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 */
	private void validateRequestTimedOut(String reqTime, Errors errors) {
		try {
			Instant reqTimeInstance = DateUtils.parseToDate(reqTime, env.getProperty("datetime.pattern")).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			Long reqDateMaxTimeLong = env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class);
			Instant maxAllowedEarlyInstant = now.minus(reqDateMaxTimeLong, ChronoUnit.HOURS);
			if (reqTimeInstance.isBefore(maxAllowedEarlyInstant)) {
				mosipLogger.debug(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- "
								+ String.format(IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage(),
										Duration.between(reqTimeInstance, now).toMinutes() - reqDateMaxTimeLong));
				errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
					INVALID_INPUT_PARAMETER + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * Check auth request.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		AuthTypeDTO authType = authRequest.getRequestedAuth();
		if (!Objects.isNull(authType)) {
			boolean anyAuthType = Stream
					.<Supplier<Boolean>>of(authType::isOtp, authType::isBio, authType::isDemo, authType::isPin)
					.anyMatch(Supplier<Boolean>::get);

			if (!anyAuthType) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, INVALID_AUTH_REQUEST);
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { AUTH_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

			} else if (authType.isDemo()) {
				checkDemoAuth(authRequest, errors);
			} else if (authType.isBio()) {
				validateBioMetadataDetails(authRequest, errors);
			}
		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + AUTH_TYPE);
			errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { AUTH_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
