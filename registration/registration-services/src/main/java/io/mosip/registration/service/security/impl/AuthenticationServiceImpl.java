package io.mosip.registration.service.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.service.security.AuthenticationService;
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

	/**
	 * Common Validator for all the Authentications
	 * @param validatorType The type of validator
	 * @param authenticationValidatorDTO The authentication validation inputs
	 * @return Boolean returning whether it is matched or not
	 */
	public Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO) {

		for (AuthenticationBaseValidator validator : authenticationBaseValidators) {
			if (validator.getClass().getName().toLowerCase().contains(validatorType.toLowerCase())) {
				return validator.validate(authenticationValidatorDTO);
			}
		}
		return false;
	}

	/**
	 * This method is used to set the Authentication validators
	 * @param authBaseValidators List of validators
	 */
	@Autowired
	public void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators) {
		this.authenticationBaseValidators = authBaseValidators;
	}

}
