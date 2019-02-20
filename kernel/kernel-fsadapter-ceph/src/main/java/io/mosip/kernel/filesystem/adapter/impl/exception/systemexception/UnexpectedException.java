package io.mosip.kernel.filesystem.adapter.impl.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.filesystem.adapter.impl.utils.PlatformErrorMessages;

/**
 * This is unexpected exception.
 *
 */
public class UnexpectedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unexpected exception.
	 */
	public UnexpectedException() {
		super();
	}

	/**
	 * Instantiates a new unexpected exception.
	 *
	 * @param message
	 *            the message
	 */
	public UnexpectedException(String message) {
		super(PlatformErrorMessages.KER_SYS_UNEXCEPTED_EXCEPTION.getCode(), message);
	}

	/**
	 * Instantiates a new unexpected exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnexpectedException(String message, Throwable cause) {
		super(PlatformErrorMessages.KER_SYS_UNEXCEPTED_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}
}
