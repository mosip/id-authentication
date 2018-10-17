package io.mosip.registration.processor.core.abstractverticle.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.abstractverticle.exception.errorcodes.AbstractVerticleErrorCodes;

public class UnsupportedEncodingException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnsupportedEncodingException() {
		super();
	}
	
	public UnsupportedEncodingException(String message) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE, message);
	}
	
	public UnsupportedEncodingException(String message, Throwable cause) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE + EMPTY_SPACE, message, cause);
	}


}
