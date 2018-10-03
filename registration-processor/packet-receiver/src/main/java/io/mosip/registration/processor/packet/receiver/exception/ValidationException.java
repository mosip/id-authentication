package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * ValidationException occurs when internal validation fails.
 *
 */
public class ValidationException extends BaseUncheckedException{
	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_VALIDATION_ERROR, message);
	}

	public ValidationException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_VALIDATION_ERROR + EMPTY_SPACE, message, cause);
	}
}
