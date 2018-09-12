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
 * {@link Exception} to be thrown when data is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullDataException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 5282175344975485527L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MosipNullDataException(MosipSecurityExceptionCodeConstants errorCode,
			MosipSecurityExceptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
