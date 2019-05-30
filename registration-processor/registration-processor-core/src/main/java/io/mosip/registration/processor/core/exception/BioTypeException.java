package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class BioTypeException extends BaseCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -729697328665083903L;

	public BioTypeException(){
		super();
	}

	public BioTypeException(String message){
		super(PlatformErrorMessages.OSI_VALIDATION_BIO_TYPE_EXCEPTION.getCode(), message);
	}

	public BioTypeException(String message, Throwable cause) {
		super(PlatformErrorMessages.OSI_VALIDATION_BIO_TYPE_EXCEPTION.getCode(), message, cause);
	}
}


