package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * PacketNotValidException occurs when the file received
 * is not as per the specified format.
 */
public class PacketNotValidException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new packet not valid exception.
	 */
	public PacketNotValidException() {
		super();
	}

	/**
	 * Instantiates a new packet not valid exception.
	 *
	 * @param message the message
	 */
	public PacketNotValidException(String message) {
		super(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getCode(), message);
	}

	/**
	 * Instantiates a new packet not valid exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PacketNotValidException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_FORMAT.getCode(), message, cause);
	}
}
