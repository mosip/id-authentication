/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.crypto.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when data is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidDataException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1650218542197755276L;
	
	/**
	 * Constructor with errorCode and errorMessage
	 * @param errorCode
	 * @param errorMessage
	 */

	public MosipInvalidDataException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
