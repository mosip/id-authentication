package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class DocumentNotFoundException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7303748392658525834L;

	public DocumentNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DocumentNotFoundException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public DocumentNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO Auto-generated constructor stub
	}
	
	

	
	
	
}
