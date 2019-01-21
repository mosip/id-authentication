package io.mosip.registration.validator;

import io.mosip.registration.dto.AuthenticationValidatorDTO;

/**
 * @author Saravanakumar G
 *
 */
public abstract class AuthenticationBaseValidator {

	/**
	 * Validate the fingerprint with the Database
	 * @param authenticationValidatorDTO
	 * @return
	 */
	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);

}
