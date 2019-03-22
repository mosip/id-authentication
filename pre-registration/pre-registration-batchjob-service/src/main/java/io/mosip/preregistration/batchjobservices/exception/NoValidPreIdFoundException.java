package io.mosip.preregistration.batchjobservices.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class NoValidPreIdFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6469856598814217956L;
	
	public NoValidPreIdFoundException(String message) {
		super("",message);
	}
	public NoValidPreIdFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoValidPreIdFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}

