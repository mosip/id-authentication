/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.crypto.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when data is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullDataException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 5282175344975485527L;

	/**
	 * Constructor with errorCode and errorMessage
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipNullDataException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
