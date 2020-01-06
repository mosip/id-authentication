package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class TemplateGenerationFailedException.
 */
public class TemplateGenerationFailedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new template generation failed exception.
	 */
	public TemplateGenerationFailedException() {
		super();
	}

	/**
	 * Instantiates a new template generation failed exception.
	 *
	 * @param message
	 *            the message
	 */
	public TemplateGenerationFailedException(String message) {
		super(PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), message);
	}

	/**
	 * Instantiates a new template generation failed exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TemplateGenerationFailedException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode() + EMPTY_SPACE, message, cause);
	}

}
