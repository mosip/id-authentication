package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when Connection is not made with server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class ConnectionException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 3585613514626311385L;


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
	public ConnectionException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
