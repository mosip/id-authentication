package io.mosip.registration.processor.packet.manager.exception.systemexception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
	 * @param message the message
	 */
	public UnexpectedException(String message) {
		super(PlatformErrorMessages.RPR_SYS_UNEXCEPTED_EXCEPTION.getCode(), message);
	}

	/**
	 * Instantiates a new unexpected exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public UnexpectedException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_UNEXCEPTED_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}
}
