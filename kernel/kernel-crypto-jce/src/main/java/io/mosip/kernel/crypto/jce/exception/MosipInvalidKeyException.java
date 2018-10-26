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
 * {@link Exception} to be thrown when key is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidKeyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 2307629710761440375L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipInvalidKeyException(MosipSecurityExceptionCodeConstant exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}
}