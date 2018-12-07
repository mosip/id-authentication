package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

@Component
public abstract class AuthenticationValidatorImplementation {
	protected String fingerPrintType;

	protected RegistrationUserDetail registrationUserDetail;

	@Autowired
	protected FingerprintValidator fingerprintValidator;

	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);

	public String getFingerPrintType() {
		return fingerPrintType;
	}

	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

}
