package io.mosip.registration.processor.stages.demodedupe;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

//remove the class when auth is fixed
@Component
public class BiometricValidation {
	public boolean validateBiometric(String duplicateUin) throws ApisResourceAccessException {
		return true;
	}
}
