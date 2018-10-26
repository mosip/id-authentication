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
public class MosipEmptyInputException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 2842524563494167519L;

	/**
	 * Constructor for MosipEmptyInputException class.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public MosipEmptyInputException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
