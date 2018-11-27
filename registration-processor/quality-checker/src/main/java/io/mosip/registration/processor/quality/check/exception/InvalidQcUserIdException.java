package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class InvalidQcUserIdException.
 */
public class InvalidQcUserIdException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid qc user id exception.
	 */
	public InvalidQcUserIdException() {
		super();
	}

	/**
	 * Instantiates a new invalid qc user id exception.
	 *
	 * @param message the message
	 */
	public InvalidQcUserIdException(String message) {
		super(PlatformErrorMessages.RPR_QCR_INVALID_QC_USER_ID.getCode(), message);
	}

	/**
	 * Instantiates a new invalid qc user id exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidQcUserIdException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_QCR_INVALID_QC_USER_ID.getCode() + EMPTY_SPACE, message, cause);
	}
}