package io.mosip.registration.validator;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;

/**
 * This class is for validating authentications
 * 
 * @author Saravanakumar G
 *
 */
public abstract class AuthenticationBaseValidator {

	/**
	 * Validate the fingerprint with the Database
	 * @param authenticationValidatorDTO The DTO which contains the data to be validated
	 * @return boolean Return whether the Validation is success or not
	 */
	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);
	
	public abstract AuthTokenDTO validate(String userId, String otp);

}
