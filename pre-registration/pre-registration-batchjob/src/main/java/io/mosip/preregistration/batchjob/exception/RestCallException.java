package io.mosip.preregistration.batchjob.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RestCallException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4166660574837976618L;
	
	public RestCallException(String message) {
		super("",message);
	}
	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public RestCallException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
