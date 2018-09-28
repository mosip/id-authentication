package org.mosip.auth.service.impl.indauth.validator;

import java.util.Arrays;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.IDType;
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
		
		IDType idTypeEnum = authRequest.getIdType();
		if (idTypeEnum != null) {
			anyIdTypePresent = true;
			if (!Arrays.asList(IDType.values()).contains(idTypeEnum))
				errors.rejectValue(null, IdAuthenticationErrorConstants.INCORRECT_IDTYPE.getErrorCode(),
						new Object[] { "idTypeEnum" },
						env.getProperty("mosip.ida.validation.message.AuthRequest.Idtype"));
		}		
	}

}
