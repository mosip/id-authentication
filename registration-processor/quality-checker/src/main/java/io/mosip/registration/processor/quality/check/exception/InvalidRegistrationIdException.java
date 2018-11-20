package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;


/**
 * The Class InvalidRegistrationIdException.
 */
public class InvalidRegistrationIdException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid registration id exception.
	 */
	public InvalidRegistrationIdException() {
		super();
	}

	/**
	 * Instantiates a new invalid registration id exception.
	 *
	 * @param message the message
	 */
	public InvalidRegistrationIdException(String message) {
		super(PlatformErrorMessages.RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode(), message);
	}

	/**
	 * Instantiates a new invalid registration id exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidRegistrationIdException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}
}