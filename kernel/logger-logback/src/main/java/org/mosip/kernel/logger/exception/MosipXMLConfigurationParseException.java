/*
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.logger.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;

/**
 * {@link Exception} to be thrown when xml is not parsed correctly
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipXMLConfigurationParseException extends BaseUncheckedException {

	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = 1509212463362472896L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipXMLConfigurationParseException(LogExeptionCodeConstants errorCode, LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
