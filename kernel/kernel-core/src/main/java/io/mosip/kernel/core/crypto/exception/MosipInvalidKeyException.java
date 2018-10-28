/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.crypto.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when key is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidKeyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -3556229489431119187L;

	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipInvalidKeyException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}