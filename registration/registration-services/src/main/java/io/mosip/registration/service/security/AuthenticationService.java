<<<<<<< HEAD
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
@Service
public class AuthenticationService {

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
=======
package io.mosip.registration.service.security;

import java.util.List;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.validator.AuthenticationBaseValidator;

public interface AuthenticationService {

	/**
	 * Common Validator for all the Authentications
	 * @param validatorType The type of validator
	 * @param authenticationValidatorDTO The authentication validation inputs
	 * @return Boolean returning whether it is matched or not
	 */
	Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO);
	
	/**
	 * Common Validator for all the Authentications
	 * @param validatorType The type of validator
	 * @param userId The userId
	 * @param otp otp entered
	 * @return {@link AuthTokenDTO} returning authtokendto
	 */
	AuthTokenDTO authValidator(String validatorType, String userId, String otp);

	/**
	 * This method is used to set the Authentication validators
	 * @param authBaseValidators List of validators
	 */
	void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators);
	
	/**
	 * This method is used to validate pwd authentication
	 * @param authenticationValidatorDTO The authentication validation inputs
	 * @return String
	 */
	String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO);

}
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
