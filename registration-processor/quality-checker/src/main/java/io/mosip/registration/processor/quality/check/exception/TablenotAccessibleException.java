package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;

public class TablenotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TablenotAccessibleException() {
		super();
	}

	public TablenotAccessibleException(String errorMessage) {
		super(PlatformErrorCodes.RPR_QCR_INVALID_REGISTRATION_ID, errorMessage);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_QCR_INVALID_REGISTRATION_ID, message, cause);
	}

}