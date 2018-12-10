package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class PacketNotFoundException.
 * 
 * @author M1039285
 */
public class PacketNotFoundException extends BaseCheckedException {

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
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 */
	public PacketNotFoundException(String code, String message) {
		super(code, message);

	}

	/**
	 * Instantiates a new packet not found exception.
	 *
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public PacketNotFoundException(String code, String message, Throwable cause) {
		super(code, message, cause);

	}
}
