package io.mosip.kernel.smsnotification.msg91.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This exception class for invalid contact number.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class MosipInvalidNumberException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = -6174268206879672695L;

	/**
	 * Constructor for MosipInvalidNumberException class.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public MosipInvalidNumberException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
