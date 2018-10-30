/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.exception;

/**
 * Exception to be thrown when date format in filename pattern is wrong
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class IllegalStateException extends BaseUncheckedException {
	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 105555532L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public IllegalStateException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public IllegalStateException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
