/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.crypto.jce.constant.MosipSecurityExceptionCodeConstant;

/**
 * {@link Exception} to be thrown when key is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullMethodException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 2418996887166493543L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipNullMethodException(MosipSecurityExceptionCodeConstant exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}

}