/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.crypto.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when data is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class InvalidDataException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1650218542197755276L;

	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
   public InvalidDataException(String errorCode, String errorMessage) {
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
	public InvalidDataException(String errorCode, String errorMessage,
			Throwable rootCause) {
		super(errorCode, errorMessage,rootCause);
	}

}
