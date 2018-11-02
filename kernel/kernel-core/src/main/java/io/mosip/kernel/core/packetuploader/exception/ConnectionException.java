package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception to be thrown when Connection is not made with server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class ConnectionException extends BaseCheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public ConnectionException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
