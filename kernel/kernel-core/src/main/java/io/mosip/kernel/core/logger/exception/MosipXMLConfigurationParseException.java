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
 * {@link Exception} to be thrown when xml is not parsed correctly
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipXMLConfigurationParseException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1509212463362472896L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipXMLConfigurationParseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
