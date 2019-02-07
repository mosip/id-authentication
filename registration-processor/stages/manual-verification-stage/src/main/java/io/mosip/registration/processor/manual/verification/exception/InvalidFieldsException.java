


package io.mosip.registration.processor.manual.verification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class InvalidFieldsException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	
	public InvalidFieldsException(String code, String message){
		super(code, message);
	}

}
