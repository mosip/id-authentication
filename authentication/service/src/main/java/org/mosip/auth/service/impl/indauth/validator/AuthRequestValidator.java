package org.mosip.auth.service.impl.indauth.validator;

import java.util.Arrays;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.dto.indauth.PinDTO;
import org.mosip.auth.core.dto.indauth.PinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * 
 * This  class validates the parameters for Authorisation Request.
 * 

 * The class {@code AuthRequestValidator} validates AuthRequestDTO
 * @author Arun Bose
 */

@Component
@PropertySource("classpath:ValidationMessages.properties")
public class AuthRequestValidator implements Validator {
	
	
	@Autowired
	private Environment env;
	
	@Autowired
	private SpringValidatorAdapter validator;
	
	boolean anyIdTypePresent = false;

	@Override
	public boolean supports(Class<?> clazz) {
           return AuthRequestDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AuthRequestDTO authRequest = (AuthRequestDTO) target;
		
		validator.validate(authRequest, errors);
		
		IdType idTypeEnum = authRequest.getIdType();
//		if (idTypeEnum != null) {
//			anyIdTypePresent = true;
//			if (!Arrays.asList(IdType.values()).contains(idTypeEnum))
//				errors.rejectValue(null, IdAuthenticationErrorConstants.INCORRECT_IDTYPE.getErrorCode(),
//						new Object[] { "idTypeEnum" },
//						env.getProperty("mosip.ida.validation.message.AuthRequest.Idtype"));
//		}
		boolean remainingAuthType = authRequest.getAuthType().getBio() || authRequest.getAuthType().getAd()
				|| authRequest.getAuthType().getPin() || authRequest.getAuthType().getId();
		if (remainingAuthType) {
			errors.rejectValue(null, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.unsupportedAuthtype"));
		}

		else if (authRequest.getAuthType().getOtp()) {
			checkOTPAuth(authRequest, errors);

		} else {
			errors.rejectValue(null, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.NoAuthtype"));

		}
	}
	/*
	 * 
	 * This method checks for the otp authorisation parameters to be present.
	 * 
	 */

	public void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {

		PinDTO pinDTO = authRequest.getPinDTO();
		if (null != pinDTO) {
			PinType pinType = pinDTO.getType();
			if (null!=pinDTO.getType() &&pinType.getType().equals(PinType.OTP.getType())) {
				String otpValue = pinDTO.getValue();
				if (otpValue != null && !(otpValue.length() == 6)) {
					errors.rejectValue("pinDTO", IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
							env.getProperty("mosip.ida.validation.message.AuthRequest.OTP.length"));
				}

			} else {
				errors.rejectValue("pinDTO",IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
						env.getProperty("mosip.ida.validation.message.AuthRequest.OTP"));
			}
		} else {
			//FIXME
                errors.rejectValue(IdAuthenticationErrorConstants.EMPTY_OTP.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		}

	}

}
