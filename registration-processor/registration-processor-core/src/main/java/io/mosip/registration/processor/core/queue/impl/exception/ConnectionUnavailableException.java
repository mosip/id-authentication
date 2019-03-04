package io.mosip.registration.processor.core.queue.impl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ConnectionUnavailableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionUnavailableException() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * @param message The exception message
	 */
	public ConnectionUnavailableException(String message) {
		super(PlatformErrorMessages.RPR_MQI_CONNECTION_UNAVAILABLE.getCode(), message);
	}

	/**
	 * @param message The exception message
	 * @param cause   The exception cause
	 */
	public ConnectionUnavailableException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_MQI_CONNECTION_UNAVAILABLE.getCode(), message, cause);
	}

}
