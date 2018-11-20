package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ResultNotFoundException.
 */
public class ResultNotFoundException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new result not found exception.
	 */
	public ResultNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new result not found exception.
	 *
	 * @param message the message
	 */
	public ResultNotFoundException(String message) {
		super(PlatformErrorMessages.RPR_QCR_RESULT_NOT_FOUND.getCode(), message);
	}

	/**
	 * Instantiates a new result not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ResultNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_QCR_RESULT_NOT_FOUND.getCode() + EMPTY_SPACE, message, cause);
	}
}