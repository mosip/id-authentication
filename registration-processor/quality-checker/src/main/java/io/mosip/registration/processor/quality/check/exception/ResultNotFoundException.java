package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

public class ResultNotFoundException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultNotFoundException() {
		super();
	}

	public ResultNotFoundException(String message) {
		super(RPRPlatformErrorCodes.RPR_QCR_RESULT_NOT_FOUND, message);
	}

	public ResultNotFoundException(String message, Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_QCR_RESULT_NOT_FOUND + EMPTY_SPACE, message, cause);
	}
}