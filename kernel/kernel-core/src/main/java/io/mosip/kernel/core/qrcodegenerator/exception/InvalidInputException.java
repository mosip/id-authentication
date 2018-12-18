package io.mosip.kernel.core.qrcodegenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class which covers the range of exception which occurs when invalid input
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class InvalidInputException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -5350213197226295789L;

	/**
	 * Constructor with errorCode, and rootCause
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public InvalidInputException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	/**
	 * Constructor with errorCode, errorMessage, and rootCause
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 * @param rootCause
	 *            Cause of this exception
	 */
	public InvalidInputException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
