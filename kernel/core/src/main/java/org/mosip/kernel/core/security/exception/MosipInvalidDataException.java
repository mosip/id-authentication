/*
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;
import org.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;

/**
 * {@link Exception} to be thrown when data is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidDataException extends BaseCheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1650218542197755276L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipInvalidDataException(MosipSecurityExceptionCodeConstants errorCode,
			MosipSecurityExceptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}
}
