package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


public class VidCreationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VidCreationException() {
		super();
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public VidCreationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	

}
