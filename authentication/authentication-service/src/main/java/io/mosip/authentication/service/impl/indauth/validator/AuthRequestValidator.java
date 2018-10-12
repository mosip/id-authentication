package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uinvalidator.UinValidator;
import io.mosip.kernel.idvalidator.vidvalidator.VidValidator;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Arun Bose
 */
@Component
@PropertySource(value = { "classpath:application-local.properties" })
public class AuthRequestValidator implements Validator {

	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";
	private static final String PIN_DTO = "pinDTO";
	private static final String VALIDATE = "validate";
	private static final String AUTH_REQUEST_VALIDATOR = "AuthRequestValidator";
	private static final String SESSION_ID = "sessionId";

	private MosipLogger mosipLogger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/** The env. */
	@Autowired
	private Environment env;

	/** The validator. */
	@Autowired
	private SpringValidatorAdapter validator;

	/** The any id type present. */
	boolean anyIdTypePresent = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.isAssignableFrom(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequest = (AuthRequestDTO) target;
		String reqTime = authRequest.getReqTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date parse = null;

		try {
			parse = simpleDateFormat.parse(reqTime);
		} catch (ParseException e) {
			errors.rejectValue("reqTime", IdAuthenticationErrorConstants.INVALID_REQUEST_TIME_FORMAT.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_REQUEST_TIME_FORMAT.getErrorMessage());
		}

		if (parse != null) {
			if (isTimestampValid(parse)) {
				validator.validate(authRequest, errors);
				validateUinVid(authRequest, errors);
				checkAuthRequest(authRequest, errors);
			} else {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
						"REQUEST_TIME_OUT - request should be reached within 24Hrs");
				errors.rejectValue("reqTime", IdAuthenticationErrorConstants.INVALID_REQUEST_TIME_OUT.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_REQUEST_TIME_OUT.getErrorMessage());
			}
		}

	}

	/**
	 * Validate UIN, VID
	 * 
	 * @param authRequest
	 * @param errors
	 */
	private void validateUinVid(AuthRequestDTO authRequest, Errors errors) {

		String idType = authRequest.getIdType();

		if (idType.equals(IdType.UIN.getType())) {
			try {
				UinValidator uinValidator = new UinValidator();
				uinValidator.validateId(authRequest.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				VidValidator vidValidator = new VidValidator();
				vidValidator.validateId(authRequest.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "INCORRECT_IDTYPE - " + idType);
			errors.rejectValue("idType", IdAuthenticationErrorConstants.INCORRECT_IDTYPE.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.Idtype"));
		}
	}

	/**
	 * Validate Auth Request
	 * 
	 * @param authRequest
	 * @param errors
	 */
	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		boolean remainingAuthType = authRequest.getAuthType().isBio() || authRequest.getAuthType().isAd()
				|| authRequest.getAuthType().isPin() || authRequest.getAuthType().isPi();
		if (remainingAuthType) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_AUTH_REQUEST - remainingAuthType is true");
			errors.rejectValue("authType", IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.unsupportedAuthtype"));
		} else if (authRequest.getAuthType().isOtp()) {
			checkOTPAuth(authRequest, errors);

		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_AUTH_REQUEST - No auth type found");
			errors.rejectValue("authType", IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.NoAuthtype"));

		}
	}

	/**
	 * This method checks for the otp authorisation parameters to be present.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	public void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {

		PinDTO pinDTO = authRequest.getPinDTO();
		if (pinDTO != null) {
			PinType pinType = pinDTO.getType();
			if (null != pinDTO.getType() && pinType.getType().equals(PinType.OTP.getType())) {
				String otpValue = pinDTO.getValue();
				if (otpValue != null && otpValue.length() != 6) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
							"INVALID_OTP - length mismatch");
					errors.rejectValue(PIN_DTO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
							IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
				}

			} else {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
						"INVALID_OTP - pinType is not OTP");
				errors.rejectValue(PIN_DTO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.OTP"));
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH, "EMPTY_OTP - OTP is empty");
			errors.rejectValue(PIN_DTO, IdAuthenticationErrorConstants.EMPTY_OTP.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		}

	}

	private boolean isTimestampValid(Date timestamp) {

		Instant reqTimeInstance = timestamp.toInstant();
		Instant now = Instant.now();

		return Duration.between(reqTimeInstance, now).toHours() <= env
				.getProperty("authrequest.received-time-allowed.in-hours", Integer.class);

	}

}
