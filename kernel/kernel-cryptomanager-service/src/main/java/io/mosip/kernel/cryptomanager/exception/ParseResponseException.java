package io.mosip.kernel.cryptomanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ParseResponseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3383837827871687253L;

	public ParseResponseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		
	}

	public ParseResponseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

}
