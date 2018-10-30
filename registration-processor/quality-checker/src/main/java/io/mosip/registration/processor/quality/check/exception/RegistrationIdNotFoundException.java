package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.quality.check.exception.utils.IISPlatformErrorCodes;

public class RegistrationIdNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegistrationIdNotFoundException() {
		super();
	}

	public RegistrationIdNotFoundException(String message) {
		super(IISPlatformErrorCodes.IIS_QCV_REGISTRATION_ID_NOT_FOUND, message);
	}

	public RegistrationIdNotFoundException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_QCV_REGISTRATION_ID_NOT_FOUND + EMPTY_SPACE, message, cause);
	}
}