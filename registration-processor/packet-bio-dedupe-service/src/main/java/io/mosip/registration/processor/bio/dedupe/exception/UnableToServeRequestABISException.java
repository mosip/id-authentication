package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class UnableToServeRequestABISException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnableToServeRequestABISException() {
		super();
	}

	public UnableToServeRequestABISException(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_UNABLE_TO_SERVE_REQUEST.getCode() + EMPTY_SPACE, errorMessage);
	}

	public UnableToServeRequestABISException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_UNABLE_TO_SERVE_REQUEST.getCode() + EMPTY_SPACE, message, cause);
	}
}
