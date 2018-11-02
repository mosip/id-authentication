package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when Configuration are not valid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class IllegalConfigurationException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 925507057490535674L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public IllegalConfigurationException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
