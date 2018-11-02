package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when there is no session
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class NoSessionException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 4820565239606121727L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public NoSessionException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
