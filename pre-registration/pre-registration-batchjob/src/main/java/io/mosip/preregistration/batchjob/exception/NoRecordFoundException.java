package io.mosip.preregistration.batchjob.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class NoRecordFoundException extends BaseUncheckedException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2209784832378345717L;

	public NoRecordFoundException(String message) {
		super("",message);
	}
	public NoRecordFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoRecordFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
