package io.mosip.kernel.core.bioapi.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class BiometricException.
 * 
 * @author Sanjay Murali
 */
public class BiometricException extends BaseCheckedException{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9125558120446752522L;

	/**
	 * Instantiates a new biometric exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public BiometricException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * Instantiates a new biometric exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public BiometricException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}
	
}
