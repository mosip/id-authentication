/**
 * 
 */
package io.mosip.kernel.auth.exception;

/**
 * @author M1049825
 *
 */
public class AuthManagerException extends Exception{

	/**
	 * 
	 */
	
	private String errorCode;
	private static final long serialVersionUID = 4060346018688709387L;
	
	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public AuthManagerException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
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
	public AuthManagerException(String errorCode, String errorMessage, Throwable rootCause) {
	}
}
