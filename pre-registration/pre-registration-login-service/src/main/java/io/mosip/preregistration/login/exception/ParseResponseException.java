package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ParseResponseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ParseResponseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

	public ParseResponseException(String errorCode, String errorMessage,Throwable rootCause) {
		super(errorCode, errorMessage,rootCause);
		
	}


}
