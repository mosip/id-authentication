package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class TemplateProcessingFailureException.
 */
public class TemplateProcessingFailureException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new template processing failure exception.
	 */
	public TemplateProcessingFailureException() {
		super();
	}

	/**
	 * Instantiates a new template processing failure exception.
	 *
	 * @param message
	 *            the message
	 */
	public TemplateProcessingFailureException(String message) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode(), message);
	}

	/**
	 * Instantiates a new template processing failure exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TemplateProcessingFailureException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode() + EMPTY_SPACE, message, cause);
	}

}
