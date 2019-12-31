package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

public class VidRevocationException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public VidRevocationException() {
		super(ResidentErrorCode.VID_REVOCATION_EXCEPTION.getErrorCode(), ResidentErrorCode.VID_REVOCATION_EXCEPTION.getErrorMessage());
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorMessage the error message
	 */
	public VidRevocationException(String errorMessage) {
		super(ResidentErrorCode.VID_REVOCATION_EXCEPTION.getErrorCode(), errorMessage);
	}

}
