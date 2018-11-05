package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for inputs lengths.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipInputLengthException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 2842522173497867519L;

	/**
	 * Constructor for MosipInputLengthException class.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public MosipInputLengthException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
