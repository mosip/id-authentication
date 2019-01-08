package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class UnableToServeRequestABISException.
 */
public class UnableToServeRequestABISException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unable to serve request ABIS exception.
	 */
	public UnableToServeRequestABISException() {
		super();
	}

	/**
	 * Instantiates a new unable to serve request ABIS exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public UnableToServeRequestABISException(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_UNABLE_TO_SERVE_REQUEST.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new unable to serve request ABIS exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnableToServeRequestABISException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_UNABLE_TO_SERVE_REQUEST.getCode() + EMPTY_SPACE, message, cause);
	}
}
