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
 * {@link Exception} to be thrown when algorithm is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNoSuchAlgorithmException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -8664587302253336954L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionCodeConstants exception code constant
	 */
	public MosipNoSuchAlgorithmException(MosipSecurityExceptionCodeConstant exceptionCodeConstants) {
		super(exceptionCodeConstants.getErrorCode(), exceptionCodeConstants.getErrorMessage());
	}

}
