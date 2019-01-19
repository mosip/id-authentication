/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.logger.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when XML is not parsed correctly
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class XMLConfigurationParseException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1509212463362472896L;

	/**
	 * Constructor with errorCode, errorMessage
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public XMLConfigurationParseException(String errorCode,
			String errorMessage) {
		super(errorCode, errorMessage);
	}

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
	public XMLConfigurationParseException(String errorCode, String errorMessage,
			Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
