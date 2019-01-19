package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when configuration is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class NullConfigurationException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -2256564750997889337L;


	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NullConfigurationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
