package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.exception.utils.RegistrationErrorCodes;

public class PrimaryValidationFailed extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public PrimaryValidationFailed() {
		super();
	}

	public PrimaryValidationFailed(String message) {
		super(RegistrationErrorCodes.AGE_CRITERIA_DOESNOT_MET , message);
	}

	public PrimaryValidationFailed(String message, Throwable cause) {
		super(RegistrationErrorCodes.AGE_CRITERIA_DOESNOT_MET  + EMPTY_SPACE, message, cause);
	}
}
