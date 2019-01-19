package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ConnectionUnavailableException.
 */
public class ConnectionUnavailableException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new connection unavailable exception.
	 */
	public ConnectionUnavailableException() {
		super();
	}

	/**
	 * Instantiates a new connection unavailable exception.
	 *
	 * @param message the message
	 */
	public ConnectionUnavailableException(String message) {
		super(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getCode(), message);
	}

	/**
	 * Instantiates a new connection unavailable exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ConnectionUnavailableException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getCode() + EMPTY_SPACE, message, cause);
	}
}
