package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for empty inputs.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipNullValueException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 2842522178894167519L;

	/**
	 * Constructor for MosipNullValueException class.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public MosipNullValueException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
