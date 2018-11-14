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

	private static final long serialVersionUID = 105555532L;


	public IllegalStateException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}


	public IllegalStateException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
