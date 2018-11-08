package io.mosip.authentication.service.impl.indauth.validator;

import java.security.InvalidParameterException;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.service.helper.DateHelper;

/**
 * 
 * @author Prem Kumar
 *
 */

@Component 
public class KycAuthRequestValidator extends BaseAuthRequestValidator{
	
	@Autowired
	private AuthRequestValidator authRequestValidator;
	
	
	
	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		KycAuthRequestDTO kycAuthRequestDTO=(KycAuthRequestDTO) target;
		
		validateConsentReq(kycAuthRequestDTO, errors);
		
		if (kycAuthRequestDTO.getAuthRequest() == null) {
			errors.rejectValue("authRequest", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "authRequest" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			authRequestValidator.validate(kycAuthRequestDTO.getAuthRequest(), errors);
		}
		
	}
	public void validateConsentReq(KycAuthRequestDTO kycAuthRequestDTO ,Errors errors)
	{
		if(!kycAuthRequestDTO.isConsentReq()) {
			errors.rejectValue("consentReq", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"consentReq"}, 
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}
	
	
		
	
}
