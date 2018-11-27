package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

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
	public UnableToAccessPathException(String code, String message) {
		super(code, message);

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
	public UnableToAccessPathException(String code, String message, Throwable cause) {
		super(code, message, cause);

	}
}
