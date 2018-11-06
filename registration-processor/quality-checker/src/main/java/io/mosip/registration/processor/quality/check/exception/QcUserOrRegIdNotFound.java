package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class QcUserOrRegIdNotFound extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QcUserOrRegIdNotFound() {
		super();
		
	}

	public QcUserOrRegIdNotFound(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		
	}

	public QcUserOrRegIdNotFound(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

	public QcUserOrRegIdNotFound(String errorMessage) {
		super(errorMessage);
		
	}
	

}
