package io.mosip.registration.processor.core.token.validation.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class AccessDeniedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new access denied exception.
	 */
	public AccessDeniedException() {
		super();

	}

	/**
	 * Instantiates a new access denied exception.
	 *
	 * @param message the message
	 */
	public AccessDeniedException(String message) {
		super(PlatformErrorMessages.RPR_AUT_ACCESS_DENIED.getCode(), message);
	}
	
	/**
	 * Instantiates a new access denied exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public AccessDeniedException(String message,Throwable cause) {
		super(PlatformErrorMessages.RPR_AUT_ACCESS_DENIED.getCode(), message,cause);
	}
}
