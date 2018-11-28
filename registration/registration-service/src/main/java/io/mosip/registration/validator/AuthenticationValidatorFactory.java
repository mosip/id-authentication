package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticationValidatorFactory {

	@Autowired
	AuthenticationValidatorImplementation authenticationValidatorImplementation;

	public AuthenticationValidatorImplementation getValidator(String validatorType) {
		if (validatorType.equals("Fingerprint")) {
			authenticationValidatorImplementation=new FingerprintValidator();
		}
		return authenticationValidatorImplementation;
	}
}
