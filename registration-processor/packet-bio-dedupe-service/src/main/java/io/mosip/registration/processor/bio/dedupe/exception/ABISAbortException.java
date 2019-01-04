package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ABISAbortException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ABISAbortException() {
		super();
	}

	public ABISAbortException(String errorMessage) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_ABORT.getCode() + EMPTY_SPACE, errorMessage);
	}

	public ABISAbortException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_BDD_ABIS_ABORT.getCode() + EMPTY_SPACE, message, cause);
	}
}
