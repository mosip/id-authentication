/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.logger.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when size string arguments are passed wrong
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalArgumentException extends BaseUncheckedException {
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
	public MosipIllegalArgumentException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
