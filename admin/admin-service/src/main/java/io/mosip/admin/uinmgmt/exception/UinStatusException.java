package io.mosip.admin.uinmgmt.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * 
 * Thrown while performing get operation for UIN status
 * 
 * @author Megha Tanga
 *
 */
public class UinStatusException extends BaseUncheckedException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2785372588639412708L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 */
	public UinStatusException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 * @param rootCause    the specified cause
	 */
	public UinStatusException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
