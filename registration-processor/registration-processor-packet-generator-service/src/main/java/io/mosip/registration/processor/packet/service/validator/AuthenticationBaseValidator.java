package io.mosip.registration.processor.packet.service.validator;

import io.mosip.registration.processor.packet.service.dto.AuthenticationValidatorDTO;

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
