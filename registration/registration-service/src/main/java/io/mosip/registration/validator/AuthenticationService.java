package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

	private AuthenticationValidatorImplementation authenticationValidatorImplementation = null;

	@Autowired
	FingerprintValidator fingerprintValidator;

	@Autowired
	OTPValidator otpValidator;

	public AuthenticationValidatorImplementation getValidator(String validatorType) {
		if (validatorType.equals("Fingerprint")) {
			authenticationValidatorImplementation = fingerprintValidator;
		} else if (validatorType.equals("otp")) {
			authenticationValidatorImplementation = otpValidator;
		}
		return authenticationValidatorImplementation;
	}
}
