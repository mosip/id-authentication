package io.mosip.registration.processor.manual.verification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class FileNotPresentException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileNotPresentException(String code, String message){
		super(code, message);
	}

}
