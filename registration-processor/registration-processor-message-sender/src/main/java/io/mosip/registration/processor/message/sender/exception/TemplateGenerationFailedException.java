package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class TemplateGenerationFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TemplateGenerationFailedException() {
		super();
	}

	public TemplateGenerationFailedException(String message) {
		super(PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), message);
	}

	public TemplateGenerationFailedException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode() + EMPTY_SPACE, message, cause);
	}

}
