package io.mosip.kernel.core.exception;

/**
 * Exception to be thrown when a directory exist which is not Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class DirectoryNotEmptyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -381238520404127950L;


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
	public DirectoryNotEmptyException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
