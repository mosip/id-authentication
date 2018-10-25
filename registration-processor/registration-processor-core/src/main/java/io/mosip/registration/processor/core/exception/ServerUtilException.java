package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.errorcodes.AbstractVerticleErrorCodes;

public class ServerUtilException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServerUtilException() {
		super();
	}
	
	public ServerUtilException(String message) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE, message);
	}
	
	public ServerUtilException(String message, Throwable cause) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE + EMPTY_SPACE, message, cause);
	}

}
