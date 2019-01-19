/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;

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
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants
	 *            exception code constant
	 */
	public MosipInvalidDataException(MosipSecurityExceptionCodeConstants exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}
}
