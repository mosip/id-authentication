package io.mosip.registration.processor.abis.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class QueueConnectionNotFound extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in destination exception.
	 */
	public QueueConnectionNotFound() {
		super();

	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public QueueConnectionNotFound(String errorMessage) {
		super(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public QueueConnectionNotFound(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getCode() + EMPTY_SPACE, message, cause);

	}
}

