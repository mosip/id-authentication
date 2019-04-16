package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

// TODO: Auto-generated Javadoc
/**
 * The Class PacketSizeNotInSyncException.
 */
public class PacketSizeNotInSyncException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new packet size not in sync exception.
	 */
	public PacketSizeNotInSyncException() {
		super();
	}

	/**
	 * Instantiates a new packet size not in sync exception.
	 *
	 * @param message
	 *            the message
	 */
	public PacketSizeNotInSyncException(String message) {
		super(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE_SYNCED.getCode(), message);
	}

	/**
	 * Instantiates a new packet size not in sync exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public PacketSizeNotInSyncException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE_SYNCED.getCode() + EMPTY_SPACE, message, cause);
	}
}
