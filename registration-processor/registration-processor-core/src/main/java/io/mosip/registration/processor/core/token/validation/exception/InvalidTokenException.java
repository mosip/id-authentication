package io.mosip.registration.processor.core.token.validation.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class InvalidTokenException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new timeout exception.
	 */
	public InvalidTokenException() {
		super();

	}

	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message the message
	 */
	public InvalidTokenException(String message) {
		super(PlatformErrorMessages.RPR_AUT_INVALID_TOKEN.getCode(), message);
	}
	
	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidTokenException(String message,Throwable cause) {
		super(PlatformErrorMessages.RPR_AUT_INVALID_TOKEN.getCode() + EMPTY_SPACE, message,cause);
	}
}