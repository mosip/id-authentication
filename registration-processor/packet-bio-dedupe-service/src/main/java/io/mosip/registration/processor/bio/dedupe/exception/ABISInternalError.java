package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ABISInternalError extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ABISInternalError() {
		super();
	}

	public ABISInternalError(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_INTERNAL_ERROR.getCode() + EMPTY_SPACE, errorMessage);
	}

	public ABISInternalError(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_INTERNAL_ERROR.getCode() + EMPTY_SPACE, message, cause);
	}
}
