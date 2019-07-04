/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;

/**
 * {@link Exception} to be thrown when key is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullKeyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -4551985646146153410L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipNullKeyException(MosipSecurityExceptionCodeConstants exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}

}