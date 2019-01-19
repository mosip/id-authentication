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
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants
	 *            exception code constant
	 */
	public MosipInvalidKeyException(MosipSecurityExceptionCodeConstants exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}
}