package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Arun Bose
 */
@Component
public class AuthRequestValidator implements Validator {

	private static final Integer OTP_LENGTH = 6;
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";
	private static final String PERSONAL_DATA_DTO = "Pii";
	private static final String VALIDATE = "validate";
	private static final String AUTH_REQUEST_VALIDATOR = "AuthRequestValidator";
	private static final String SESSION_ID = "sessionId";

	private static MosipLogger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The validator. */
	@Autowired
	private SpringValidatorAdapter validator;

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
				UinValidatorImpl uinValidator = new UinValidatorImpl();
				uinValidator.validateId(authRequest.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				VidValidatorImpl vidValidator = new VidValidatorImpl();
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
		AuthTypeDTO authType = authRequest.getAuthType();
		boolean anyAuthType = Stream.<Supplier<Boolean>>of(
										authType::isOtp, 
										authType::isBio,
										authType::isAd,
										authType::isFad,
										authType::isPin,
										authType::isPi)
									.anyMatch(Supplier<Boolean>::get);

			
		if(!anyAuthType) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_AUTH_REQUEST - No auth type found");
			errors.rejectValue("authType", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "authType"));

		}
		
		if (authType.isOtp()) {
			checkOTPAuth(authRequest, errors);
		} 
	}

	/**
	 * This method checks for the otp authorisation parameters to be present.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	public void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {

		PinDTO pinDTO = null;
		if (authRequest.getPii() != null && (pinDTO = authRequest.getPii().getPin()) != null) {
			PinType pinType = pinDTO.getType();
			if (null != pinDTO.getType() && pinType.getType().equals(PinType.OTP.getType())) {
				String otpValue = pinDTO.getValue();
				if (otpValue != null && otpValue.length() != OTP_LENGTH) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
							"INVALID_OTP - length mismatch");
					errors.rejectValue(PERSONAL_DATA_DTO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
							IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
				}

			} else {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
						"INVALID_OTP - pinType is not OTP");
				errors.rejectValue(PERSONAL_DATA_DTO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.OTP"));
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH, "EMPTY_OTP - OTP is empty");
			errors.rejectValue(PERSONAL_DATA_DTO, IdAuthenticationErrorConstants.EMPTY_OTP.getErrorCode(),
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
