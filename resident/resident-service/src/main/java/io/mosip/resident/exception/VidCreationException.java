package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;


public class VidCreationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VidCreationException() {
		super(ResidentErrorCode.VID_CREATION_EXCEPTION.getErrorCode(), ResidentErrorCode.VID_CREATION_EXCEPTION.getErrorMessage());
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorMessage the error message
	 */
	public VidCreationException(String errorMessage) {
		super(ResidentErrorCode.VID_CREATION_EXCEPTION.getErrorCode(), errorMessage);
	}
	

}
