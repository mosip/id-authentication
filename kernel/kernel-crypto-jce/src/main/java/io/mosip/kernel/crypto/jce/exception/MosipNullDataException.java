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
 * {@link Exception} to be thrown when data is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullDataException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -4388634537745020250L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipNullDataException(MosipSecurityExceptionCodeConstant exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}

}
