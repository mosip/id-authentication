package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ResidentServiceException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 8621530697947108810L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 */
	public ResidentServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 * @param rootCause    the specified cause
	 */
	public ResidentServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
