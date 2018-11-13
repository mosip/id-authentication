package io.mosip.registration.processor.packet.receiver.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

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
		super(IISPlatformErrorCodes.RPR_PKR_TIMEOUT, message);
	}
	
	/**
	 * Instantiates a new timeout exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public TimeoutException(String message,Throwable cause) {
		super(IISPlatformErrorCodes.RPR_PKR_TIMEOUT + EMPTY_SPACE, message,cause);
	}
}