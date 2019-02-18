package io.mosip.registration.processor.core.queue.impl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class InvalidConnectionException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor
	 */
	public InvalidConnectionException() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	/**
	 * @param message The exception message
	 */
	public InvalidConnectionException(String message){
		super(PlatformErrorMessages.RPR_MQI_INVALID_CONNECTION.getCode(), message);
	}
	
	/**
	 * @param message The exception message
	 * @param cause The cause
	 */
	public InvalidConnectionException(String message, Throwable cause){
		super(PlatformErrorMessages.RPR_MQI_INVALID_CONNECTION.getCode(), message, cause);
	}

}
