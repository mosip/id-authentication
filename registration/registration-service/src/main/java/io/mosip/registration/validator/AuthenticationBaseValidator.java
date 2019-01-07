package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

/**
 * @author Saravanakumar G
 *
 */
public abstract class AuthenticationBaseValidator {

	protected RegistrationUserDetail registrationUserDetail;

	@Autowired
	protected FingerprintValidatorImpl fingerprintValidator;

	/**
	 * Validate the fingerprint with the Database
	 * @param authenticationValidatorDTO
	 * @return
	 */
	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);

}
