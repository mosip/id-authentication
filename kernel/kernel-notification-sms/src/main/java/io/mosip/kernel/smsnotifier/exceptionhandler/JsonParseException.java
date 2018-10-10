package io.mosip.kernel.smsnotifier.exceptionhandler;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class JsonParseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8161085174042890973L;

	public JsonParseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	
	}
	

}
