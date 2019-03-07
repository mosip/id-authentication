package io.mosip.authentication.service.impl.indauth.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
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

	private static final String ALLOWED_AUTH_TYPE = "allowed.auth.type";

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
	private static final String REQ_TIME = "requestTime";

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
				validateAllowedAuthTypes(authRequestDto, errors, ALLOWED_AUTH_TYPE);
			}
			if (!errors.hasErrors()) {
			validateReqTime(authRequestDto.getRequestTime(), errors);
			}
			if (!errors.hasErrors()) {
			validateTxnId(authRequestDto.getTransactionID(), errors);
			}
			if (!errors.hasErrors()) {
				validateAuthType(authRequestDto.getRequestedAuth(), errors);
			}
			if (!errors.hasErrors()) {
				validateRequestTimedOut(authRequestDto.getRequestTime(), errors);
			}

			if (!errors.hasErrors()) {
				super.validate(target, errors);
				
				Optional<String> uinOpt = Optional.ofNullable(authRequestDto.getRequest())
						.map(RequestDTO::getIdentity)
						.map(IdentityDTO::getUin);
				Optional<String> vidOpt = Optional.ofNullable(authRequestDto.getRequest())
						.map(RequestDTO::getIdentity)
						.map(IdentityDTO::getVid);
				if(uinOpt.isPresent()) {
					validateIdvId(uinOpt.get(), IdType.UIN.getType(), errors);
				} else if(vidOpt.isPresent()) {
					validateIdvId(vidOpt.get(), IdType.VID.getType(), errors);
				} else {
					// TODO Missing UIN/VID
				}
				if (!errors.hasErrors()) {
					checkAuthRequest(authRequestDto, errors);
				}
			}
		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Validate request timed out.
	 *
	 * @param reqTime
	 *            the req time
	 * @param errors
	 *            the errors
	 */
	private void validateRequestTimedOut(String reqTime, Errors errors) {
		try {
			Instant reqTimeInstance = DateUtils.parseToDate(reqTime,env.getProperty("datetime.pattern")).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			Integer reqDateMaxTimeInt = env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class);
			Long reqDateMaxTimeLong = env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class);
			if (null != reqDateMaxTimeInt && null != reqDateMaxTimeLong
					&& Duration.between(reqTimeInstance, now).toHours() > reqDateMaxTimeInt) {
				mosipLogger.debug(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- " + String.format(
								IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes() - reqDateMaxTimeLong));
				errors.rejectValue(REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorCode(),
						new Object[] { env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class) },
						IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException | java.text.ParseException e) {
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
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
	 */
	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		AuthTypeDTO authType = authRequest.getRequestedAuth();
		if (!Objects.isNull(authType)) {
			boolean anyAuthType = Stream
					.<Supplier<Boolean>>of(authType::isOtp, authType::isBio, 
							authType::isDemo, authType::isPin)
					.anyMatch(Supplier<Boolean>::get);

			if (!anyAuthType) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, INVALID_AUTH_REQUEST);
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { AUTH_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

			} else if (authType.isOtp()) {
				checkOTPAuth(authRequest, errors);
			} else if (authType.isDemo()) {
				checkDemoAuth(authRequest, errors);
			} else if (authType.isPin() || authType.isOtp()) {
				validateAdditionalFactorsDetails(authRequest, errors);
			} else if (authType.isBio()) {
				validateBioMetadataDetails(authRequest, errors);
			}
		} else {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + AUTH_TYPE);
			errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { AUTH_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

}
