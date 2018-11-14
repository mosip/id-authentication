package io.mosip.registration.processor.packet.manager.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

/**
 * This is BadGatewayException.
 *
 */
public class BadGatewayException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new bad gateway exception.
	 */
	public BadGatewayException() {
		super();
	}

	/**
	 * Instantiates a new bad gateway exception.
	 *
	 * @param message the message
	 */
	public BadGatewayException(String message) {
		super(RPRPlatformErrorCodes.RPR_PKM_BAD_GATEWAY, message);
	}

	/**
	 * Instantiates a new bad gateway exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public BadGatewayException(String message,Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_PKM_BAD_GATEWAY + EMPTY_SPACE, message,cause);
	}
}
