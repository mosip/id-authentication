package io.mosip.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.validator.AuthenticationBaseValidator;

/**
 * @author SaravanaKumar G
 *
 */
@Service
public class AuthenticationService {

	@Autowired
	@Qualifier("fingerprintValidator")
	AuthenticationBaseValidator fingerprintValidator;

	@Autowired
	@Qualifier("oTPValidatorImpl")
	AuthenticationBaseValidator otpValidator;

	public Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO) {
		
		if (validatorType.equals("Fingerprint")) {
			return fingerprintValidator.validate(authenticationValidatorDTO);
		} else if (validatorType.equals("otp")) {
			return otpValidator.validate(authenticationValidatorDTO);
		}
		return false;
	}
}
