/*
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;

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
	private static final long serialVersionUID = 24357407021151065L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipNoSuchAlgorithmException(MosipSecurityExceptionCodeConstants errorCode,
			MosipSecurityExceptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
