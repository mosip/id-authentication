package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

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
		super(RPRPlatformErrorCodes.RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE, message);

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
		super(RPRPlatformErrorCodes.RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE, message, cause);

	}
}
