package io.mosip.preregistration.translitration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.translitration.errorcode.ErrorCodes;

public class FailedToTranslitrateException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118051836960387612L;
	
	public FailedToTranslitrateException() {
		super();
	}
	
	public FailedToTranslitrateException(String errorCode,String message) {
		super(errorCode,message);
	}

}
