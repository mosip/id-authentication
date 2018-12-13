package io.mosip.registration.processor.manual.adjudication.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class NoRecordAssignedException extends BaseUncheckedException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoRecordAssignedException(String code, String message){
		super(code, message);
	}

}
