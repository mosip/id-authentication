package io.mosip.registration.processor.core.exception;

import java.net.UnknownHostException;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.errorcodes.AbstractVerticleErrorCodes;

public class ServerUtilException extends BaseUncheckedException {

	/**
	 * @author M1048860
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServerUtilException() {
		super();
	}
	
	public ServerUtilException(UnknownHostException e) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_UNKNOWN_EXCEPTION, e.toString());
	}
	
	

}
