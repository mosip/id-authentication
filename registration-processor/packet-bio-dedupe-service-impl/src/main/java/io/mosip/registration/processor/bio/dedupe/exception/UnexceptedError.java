package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class UnexceptedError.
 */
public class UnexceptedError extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unexcepted error.
	 */
	public UnexceptedError() {
		super();
	}

	/**
	 * Instantiates a new unexcepted error.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public UnexceptedError(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_UNEXCEPTED_ERROR.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new unexcepted error.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnexceptedError(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_UNEXCEPTED_ERROR.getCode() + EMPTY_SPACE, message, cause);
	}
}
