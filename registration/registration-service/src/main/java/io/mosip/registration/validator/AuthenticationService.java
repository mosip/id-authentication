package io.mosip.registration.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author SaravanaKumar G
 *
 */
@Component
public class AuthenticationService {

	@Autowired
	private AuthenticationValidatorImplementation authenticationValidatorImplementation;

	private List<AuthenticationValidatorImplementation> authenticationValidators;

	@Autowired
	FingerprintValidator fingerprintValidator;

	@Autowired
	OTPValidator otpValidator;

	/**
	 * It will set all the validators which extends the AuthenticationValidatorImplementation
	 * 			in this list
	 * @param authenticationValidatorImplementations
	 */
	@Autowired
	public void setAuthenticationValidatorImplementation(
			List<AuthenticationValidatorImplementation> authenticationValidatorImplementations) {
		this.authenticationValidators = authenticationValidatorImplementations;
	}

	/**
	 * It will return the respective validator.
	 * @param validatorType
	 * @return
	 */
	public AuthenticationValidatorImplementation getValidator(String validatorType) {
		for (AuthenticationValidatorImplementation validator : authenticationValidators) {
			if (validator.getClass().getName().toLowerCase().contains(validatorType.toLowerCase())) {
				authenticationValidatorImplementation = validator;
			}
		}

		return authenticationValidatorImplementation;
	}
}
