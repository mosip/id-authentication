package io.mosip.registration.service.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.validator.AuthenticationBaseValidator;

public interface AuthenticationServiceImpl {

	/**
	 * Common Validator for all the Authentications
	 * @param validatorType The type of validator
	 * @param authenticationValidatorDTO The authentication validation inputs
	 * @return Boolean returning whether it is matched or not
	 */
	Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO);

	/**
	 * This method is used to set the Authentication validators
	 * @param authBaseValidators List of validators
	 */
	void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators);

}