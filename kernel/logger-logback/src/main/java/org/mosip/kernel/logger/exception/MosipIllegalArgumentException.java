/*
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.logger.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;

/**
 * Exception to be thrown when size string arguments are passed wrong
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalArgumentException extends BaseUncheckedException {
	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = 105555532L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipIllegalArgumentException(LogExeptionCodeConstants errorCode, LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}
}
