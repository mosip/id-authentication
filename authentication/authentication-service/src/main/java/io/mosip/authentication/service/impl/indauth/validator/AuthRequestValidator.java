package io.mosip.authentication.service.impl.indauth.validator;

import org.springframework.beans.factory.annotation.Autowired;
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

// TODO: Auto-generated Javadoc
/**
 * 
 * This class validates the parameters for Authorisation Request.
 * 
 * 
 * The class {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Arun Bose
 */
@Component
public class AuthRequestValidator implements Validator {
	
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

		validator.validate(authRequest, errors);

		String idType = authRequest.getIdType();

		if (idType.equals(IdType.UIN.getType())) {
			try {
				UinValidator uinValidator = new UinValidator();
				uinValidator.validateId(authRequest.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error("sessionId", "AuthRequestValidator", "validate()", "MosipInvalidIDException - " + e);
				// FIXME switch for other errocodes thrown in MosipInvalidIDException
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.id"));
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				VidValidator vidValidator = new VidValidator();
				vidValidator.validateId(authRequest.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error("sessionId", "AuthRequestValidator", "validate()", "MosipInvalidIDException - " + e);
				// FIXME switch for other errocodes thrown in MosipInvalidIDException
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.id"));
			}
		} else {
			mosipLogger.error("sessionId", "AuthRequestValidator", "validate()", "INCORRECT_IDTYPE - " + idType);
			errors.rejectValue("idType", IdAuthenticationErrorConstants.INCORRECT_IDTYPE.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.Idtype"));
		}

		boolean remainingAuthType = authRequest.getAuthType().isBio() || authRequest.getAuthType().isAd()
				|| authRequest.getAuthType().isPin() || authRequest.getAuthType().isId();
		if (remainingAuthType) {
			mosipLogger.error("sessionId", "AuthRequestValidator", "validate()", "INVALID_AUTH_REQUEST - remainingAuthType is true");
			errors.rejectValue("authType", IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.unsupportedAuthtype"));
		}

		else if (authRequest.getAuthType().isOtp()) {
			checkOTPAuth(authRequest, errors);

		} else {
			mosipLogger.error("sessionId", "AuthRequestValidator", "validate()", "INVALID_AUTH_REQUEST - No auth type found");
			errors.rejectValue("authType", IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.NoAuthtype"));

		}
	}

	/**
	 * This method checks for the otp authorisation parameters to be present.
	 *
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
	 */
	public void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {

		PinDTO pinDTO = authRequest.getPinDTO();
		if (pinDTO != null) {
			PinType pinType = pinDTO.getType();
			if (null != pinDTO.getType() && pinType.getType().equals(PinType.OTP.getType())) {
				String otpValue = pinDTO.getValue();
				if (otpValue != null && otpValue.length() != 6) {
					mosipLogger.error("sessionId", "AuthRequestValidator", "validate() -> checkOTPAuth()", "INVALID_OTP - length mismatch");
					errors.rejectValue("pinDTO", IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
							IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
				}

			} else {
				mosipLogger.error("sessionId", "AuthRequestValidator", "validate() -> checkOTPAuth()", "INVALID_OTP - pinType is not OTP");
				errors.rejectValue("pinDTO", IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.OTP"));
			}
		} else {
			// FIXME
			mosipLogger.error("sessionId", "AuthRequestValidator", "validate() -> checkOTPAuth()", "EMPTY_OTP - OTP is empty");
			errors.rejectValue("pinDTO", IdAuthenticationErrorConstants.EMPTY_OTP.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		}

	}

}
