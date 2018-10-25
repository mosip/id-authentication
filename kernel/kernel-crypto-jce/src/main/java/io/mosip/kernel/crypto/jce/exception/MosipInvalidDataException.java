/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.crypto.jce.constant.MosipSecurityExceptionCodeConstant;

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
	private static final long serialVersionUID = 3300234777225219678L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipInvalidDataException(MosipSecurityExceptionCodeConstant exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}
}
