package io.mosip.kernel.filesystem.adapter.impl.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.filesystem.adapter.impl.utils.PlatformErrorMessages;

/**
 * TimeoutException occurs when time exceeds specified time limit.
 *
 */
public class TimeoutException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new timeout exception.
	 */
	public TimeoutException() {
		super();

	}

	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message
	 *            the message
	 */
	public TimeoutException(String message) {
		super(PlatformErrorMessages.KER_SYS_TIMEOUT_EXCEPTION.getCode(), message);
	}

	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TimeoutException(String message, Throwable cause) {
		super(PlatformErrorMessages.KER_SYS_TIMEOUT_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}