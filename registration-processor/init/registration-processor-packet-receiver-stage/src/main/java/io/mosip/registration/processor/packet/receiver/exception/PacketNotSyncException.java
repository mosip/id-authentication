package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class PacketNotSyncException.
 */
public class PacketNotSyncException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new packet not sync exception.
	 */
	public PacketNotSyncException() {
		super();
	}

	/**
	 * Instantiates a new packet not sync exception.
	 *
	 * @param message the message
	 */
	public PacketNotSyncException(String message) {
		super(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getCode(), message);
	}

	/**
	 * Instantiates a new packet not sync exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PacketNotSyncException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKR_PACKET_NOT_YET_SYNC.getCode() + EMPTY_SPACE, message, cause);
	}
}