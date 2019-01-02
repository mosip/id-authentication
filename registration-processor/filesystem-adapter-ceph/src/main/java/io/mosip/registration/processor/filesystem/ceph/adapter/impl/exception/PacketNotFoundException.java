package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;


import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * PacketNotFoundException occurs when requested packet is not present
 * in DFS.
 */
public class PacketNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new packet not found exception.
	 */
	public PacketNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new packet not found exception.
	 *
	 * @param message the message
	 */
	public PacketNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_FAC_PACKET_NOT_AVAILABLE.getCode() + EMPTY_SPACE, message);
	}

	/**
	 * Instantiates a new packet not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PacketNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_FAC_PACKET_NOT_AVAILABLE.getCode(), message, cause);
	}
}