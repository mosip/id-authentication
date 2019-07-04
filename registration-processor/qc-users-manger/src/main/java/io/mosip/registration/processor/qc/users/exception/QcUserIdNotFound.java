package io.mosip.registration.processor.qc.users.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class QcUserIdNotFound extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QcUserIdNotFound() {
		super();
		
	}

	public QcUserIdNotFound(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		
	}

	public QcUserIdNotFound(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

	public QcUserIdNotFound(String errorMessage) {
		super(errorMessage);
		
	}
	

}
