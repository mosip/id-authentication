package io.mosip.registration.processor.qc.users.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.qc.users.exception.utils.IISPlatformErrorCodes;

public class ResultNotFoundException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultNotFoundException() {
		super();
	}

	public ResultNotFoundException(String message) {
		super(IISPlatformErrorCodes.IIS_QCV_RESULT_NOT_FOUND, message);
	}

	public ResultNotFoundException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_QCV_RESULT_NOT_FOUND + EMPTY_SPACE, message, cause);
	}
}