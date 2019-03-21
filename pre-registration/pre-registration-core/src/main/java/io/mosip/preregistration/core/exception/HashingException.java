package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class HashingException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 43021822814026167L;

	public HashingException() {
		super();
	}

	public HashingException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public HashingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public HashingException(String errorMessage) {
		super(errorMessage);
	}
	

}
