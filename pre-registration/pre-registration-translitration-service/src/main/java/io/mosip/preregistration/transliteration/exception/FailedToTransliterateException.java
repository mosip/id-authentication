package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class FailedToTransliterateException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118051836960387612L;
	
	public FailedToTransliterateException() {
		super();
	}
	
	public FailedToTransliterateException(String errorCode,String message) {
		super(errorCode,message);
	}

}
