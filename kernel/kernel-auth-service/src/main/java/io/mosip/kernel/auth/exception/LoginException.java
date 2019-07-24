/**
 * 
 */
package io.mosip.kernel.auth.exception;

/**
 * @author Ramadurai Pandian
 *
 */
public class LoginException extends RuntimeException {

	/**
	 * 
	 */

	private String errorCode;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public LoginException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorMessage, rootCause);
		this.errorCode = errorCode;
	}

	private static final long serialVersionUID = 4060346018688709387L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 */
	public LoginException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}
}
