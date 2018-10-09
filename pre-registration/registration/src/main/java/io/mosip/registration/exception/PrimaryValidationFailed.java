package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.exception.utils.RegistrationErrorCodes;

public class PrimaryValidationFailed extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public PrimaryValidationFailed() {
		super();
	}

	public PrimaryValidationFailed(String message) {
		super(RegistrationErrorCodes.IIS_EPU_ATU_REGISTRATION_TABLE_NOTACCESSIBLE, message);
	}

	public PrimaryValidationFailed(String message, Throwable cause) {
		super(RegistrationErrorCodes.IIS_EPU_ATU_REGISTRATION_TABLE_NOTACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}
