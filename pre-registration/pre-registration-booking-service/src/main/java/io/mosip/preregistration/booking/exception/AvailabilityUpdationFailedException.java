package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class AvailabilityUpdationFailedException extends BaseUncheckedException{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8740121632422570371L;

	public AvailabilityUpdationFailedException(String msg) {
		super("", msg);
	}

	public AvailabilityUpdationFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AvailabilityUpdationFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AvailabilityUpdationFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AvailabilityUpdationFailedException() {
		super();
	}
}
