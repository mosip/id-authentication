<<<<<<< HEAD
package io.mosip.registration.service.security;

import java.util.List;

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

=======
package io.mosip.registration.service.security;

import java.util.List;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.validator.AuthenticationBaseValidator;

public interface AuthenticationService {

	/**
	 * Common Validator for all the Authentications
	 * 
	 * @param validatorType
	 *            The type of validator
	 * @param authenticationValidatorDTO
	 *            The authentication validation inputs
	 * @return Boolean returning whether it is matched or not
	 */
	Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO);
	
	/**
	 * Validator for OTP authentication
	 * 
	 * @param validatorType
	 *            The type of validator which is OTP
	 * @param userId
	 *            The userId
	 * @param otp
	 *            otp entered
	 * @return {@link AuthTokenDTO} returning authtokendto
	 */
	AuthTokenDTO authValidator(String validatorType, String userId, String otp);

	/**
	 * This method is used to set the Authentication validators
	 * 
	 * @param authBaseValidators
	 *            List of validators
	 */
	void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators);
	
	/**
	 * This method is used to validate pwd authentication
	 * 
	 * @param authenticationValidatorDTO
	 *            The authentication validation inputs with user id and pwd
	 * @return String
	 */
	String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO);

>>>>>>> 55442bec8b0b7257e86524eff51c77f99a33dc9f
}