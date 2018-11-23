package io.mosip.registration.processor.packet.receiver.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
	 * @param message the message
	 */
	public TimeoutException(String message) {
		super(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getCode(), message);
	}
	
	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public TimeoutException(String message,Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getCode() + EMPTY_SPACE, message,cause);
	}
}