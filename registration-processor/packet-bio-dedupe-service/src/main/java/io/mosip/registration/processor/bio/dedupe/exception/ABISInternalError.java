package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ABISInternalError.
 */
public class ABISInternalError extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new ABIS internal error.
	 */
	public ABISInternalError() {
		super();
	}

	/**
	 * Instantiates a new ABIS internal error.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public ABISInternalError(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_INTERNAL_ERROR.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new ABIS internal error.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public ABISInternalError(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_INTERNAL_ERROR.getCode() + EMPTY_SPACE, message, cause);
	}
}
