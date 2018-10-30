package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.quality.check.exception.utils.IISPlatformErrorCodes;

public class InvalidRegistrationIdException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRegistrationIdException() {
		super();
	}

	public InvalidRegistrationIdException(String message) {
		super(IISPlatformErrorCodes.IIS_QCV_INVALID_REGISTRATION_ID, message);
	}

	public InvalidRegistrationIdException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_QCV_INVALID_REGISTRATION_ID + EMPTY_SPACE, message, cause);
	}
}