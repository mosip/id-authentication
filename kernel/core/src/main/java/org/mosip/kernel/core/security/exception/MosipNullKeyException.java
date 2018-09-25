/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;

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
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipNullKeyException(MosipSecurityExceptionCodeConstants errorCode,
			MosipSecurityExceptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}