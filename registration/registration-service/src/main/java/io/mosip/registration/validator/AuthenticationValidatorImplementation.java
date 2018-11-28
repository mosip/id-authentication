package io.mosip.registration.validator;

import io.mosip.registration.dto.AuthenticationValidatorDTO;

public abstract class AuthenticationValidatorImplementation {
	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);
}
