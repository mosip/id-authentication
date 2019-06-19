package io.mosip.authentication.common.service.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
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

	private static final int FINGERPRINT_COUNT = 2;

	private static final String REQUEST_REQUEST_TIME = "request/timestamp";

	/** The Constant AUTH_REQUEST. */
	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant VALIDATE_REQUEST_TIMED_OUT. */
	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

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
				validateAllowedAuthTypes(authRequestDto, errors, getAllowedAuthTypeProperty());
			}
			if (!errors.hasErrors()) {
				validateReqTime(authRequestDto.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);
				// Validation for Time Stamp in the RequestDTO.
				validateReqTime(authRequestDto.getRequest().getTimestamp(), errors, REQUEST_REQUEST_TIME);
			}
			if (!errors.hasErrors()) {
				validateTxnId(authRequestDto.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);
			}
			if (!errors.hasErrors()) {
				validateAuthType(authRequestDto.getRequestedAuth(), errors);
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
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, IdAuthCommonConstants.INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	@Override
	protected void validateReqTime(String reqTime, Errors errors, String paramName) {
		super.validateReqTime(reqTime, errors, paramName);
		if (!errors.hasErrors()) {
			validateRequestTimedOut(reqTime, errors);
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
			Instant reqTimeInstance = DateUtils
					.parseToDate(reqTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			Long reqDateMaxTimeLong = env
					.getProperty(IdAuthConfigKeyConstants.AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES, Long.class);
			Instant maxAllowedEarlyInstant = now.minus(reqDateMaxTimeLong, ChronoUnit.MINUTES);
			if (reqTimeInstance.isBefore(maxAllowedEarlyInstant)) {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- "
								+ String.format(IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(),
										Duration.between(reqTimeInstance, now).toMinutes() - reqDateMaxTimeLong));
				errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode(),
						new Object[] { reqDateMaxTimeLong },
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQ_TIME);
			errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IdAuthCommonConstants.REQ_TIME },
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
		if (authType.isDemo()) {
			checkDemoAuth(authRequest, errors);
		} else if (authType.isBio()) {
			Set<String> allowedAuthType = getAllowedAuthTypes(getAllowedAuthTypeProperty());
			validateBioMetadataDetails(authRequest, errors, allowedAuthType);
		}
	}

	/**
	 * @return the allowedAuthType
	 */
	protected String getAllowedAuthTypeProperty() {
		return IdAuthConfigKeyConstants.ALLOWED_AUTH_TYPE;
	}

	@Override
	protected int getMaxFingerCount() {
		return FINGERPRINT_COUNT;
	}

}
