package io.mosip.registration.service.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.validator.AuthenticationBaseValidator;

/**
 * Service class for Authentication
 * 
 * @author SaravanaKumar G
 *
 */

public interface AuthenticationService {

	Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO);

	void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators);

}
