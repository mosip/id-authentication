package io.mosip.registration.service.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.service.security.impl.AuthenticationService;
import io.mosip.registration.validator.AuthenticationBaseValidator;

/**
 * Service class for Authentication
 * 
 * @author SaravanaKumar G
 *
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private List<AuthenticationBaseValidator> authenticationBaseValidators;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#authValidator(java.lang.String, io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO) {

		for (AuthenticationBaseValidator validator : authenticationBaseValidators) {
			if (validator.getClass().getName().toLowerCase().contains(validatorType.toLowerCase())) {
				return validator.validate(authenticationValidatorDTO);
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#setAuthenticationBaseValidator(java.util.List)
	 */
	@Override
	@Autowired
	public void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators) {
		this.authenticationBaseValidators = authBaseValidators;
	}

}
