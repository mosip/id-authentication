package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

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
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_PACKET, message);
	}

	/**
	 * Instantiates a new packet not valid exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PacketNotValidException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_PACKET, message, cause);
	}
}
