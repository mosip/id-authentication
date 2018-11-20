package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class UnableToAccessPathException.
 */
public class UnableToAccessPathException extends BaseCheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unable to access path exception.
	 */
	public UnableToAccessPathException() {
		super();

	}

	/**
	 * Instantiates a new unable to access path exception.
	 *
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 */
	public UnableToAccessPathException(String message) {
		super(PlatformErrorMessages.RPR_PDJ_PACKET_DECRYPTION_FAILURE.getCode(), message);

	}

	/**
	 * Instantiates a new unable to access path exception.
	 *
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnableToAccessPathException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PDJ_PACKET_DECRYPTION_FAILURE.getCode(), message, cause);

	}
}
