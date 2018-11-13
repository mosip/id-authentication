package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * PacketNotAvailableException occurs when the file is
 * not present in the request.
 */
public class PacketNotAvailableException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new packet not available exception.
	 */
	public PacketNotAvailableException() {
		super();
	}

	/**
	 * Instantiates a new packet not available exception.
	 *
	 * @param message the message
	 */
	public PacketNotAvailableException(String message) {
		super(IISPlatformErrorCodes.RPR_PKR_PACKET_NOT_AVAILABLE, message);
	}

	/**
	 * Instantiates a new packet not available exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PacketNotAvailableException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.RPR_PKR_PACKET_NOT_AVAILABLE + EMPTY_SPACE, message, cause);
	}
}
