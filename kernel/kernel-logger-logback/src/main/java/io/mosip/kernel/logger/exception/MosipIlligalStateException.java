/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.logger.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.kernel.logger.constant.LogExeptionCodeConstants;

/**
 * Exception to be thrown when date format in filename pattern is wrong
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIlligalStateException extends BaseUncheckedException {
	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 105555532L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipIlligalStateException(LogExeptionCodeConstants errorCode,
			LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}
}
