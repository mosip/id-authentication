package io.mosip.admin.uinmgmt.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class UinDetailNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8720020780479867131L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public UinDetailNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 * @param rootCause
	 *            the specified cause
	 */
	public UinDetailNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
