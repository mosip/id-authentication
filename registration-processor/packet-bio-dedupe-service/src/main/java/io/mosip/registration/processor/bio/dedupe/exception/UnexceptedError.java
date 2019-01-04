package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class UnexceptedError extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnexceptedError() {
		super();
	}

	public UnexceptedError(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_UNEXCEPTED_ERROR.getCode() + EMPTY_SPACE, errorMessage);
	}

	public UnexceptedError(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_UNEXCEPTED_ERROR.getCode() + EMPTY_SPACE, message, cause);
	}
}
