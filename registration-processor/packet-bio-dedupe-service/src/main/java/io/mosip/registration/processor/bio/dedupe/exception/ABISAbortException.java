package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ABISAbortException.
 */
public class ABISAbortException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new ABIS abort exception.
	 */
	public ABISAbortException() {
		super();
	}

	/**
	 * Instantiates a new ABIS abort exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public ABISAbortException(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_ABORT.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new ABIS abort exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ABISAbortException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_ABORT.getCode() + EMPTY_SPACE, message, cause);
	}
}
