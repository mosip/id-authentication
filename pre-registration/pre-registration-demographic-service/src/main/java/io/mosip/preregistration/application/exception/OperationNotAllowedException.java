/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the OperationNotAllowedException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class OperationNotAllowedException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param errorMessage pass the error message
	 */
	public OperationNotAllowedException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public OperationNotAllowedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public OperationNotAllowedException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public OperationNotAllowedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
