package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class TemplateProcessingFailureException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	
	public TemplateProcessingFailureException() {
		super();
	}

	
	public TemplateProcessingFailureException(String message) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode(), message);
	}

	
	public TemplateProcessingFailureException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode() + EMPTY_SPACE, message, cause);
	}

}
