package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationValidatorFactory {

	private AuthenticationValidatorImplementation authenticationValidatorImplementation=null;
	
	@Autowired
	FingerprintValidator fingerprintValidator;

	public AuthenticationValidatorImplementation getValidator(String validatorType) {
		if (validatorType.equals("Fingerprint")) {
			authenticationValidatorImplementation = fingerprintValidator;
		}
		return authenticationValidatorImplementation;
	}
}
