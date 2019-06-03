package io.mosip.registration.processor.biodedupe.stage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class CbeffNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new identity not found exception.
	 */
	public CbeffNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new identity not found exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public CbeffNotFoundException(String errorMessage) {
		super(PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new identity not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CbeffNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.PACKET_BIO_DEDUPE_CBEFF_NOT_PRESENT.getCode() + EMPTY_SPACE, message, cause);
	}

}
