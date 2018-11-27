package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when Path is Null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class NullPathException extends BaseUncheckedException {


	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -2576609623548108679L;


	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NullPathException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
