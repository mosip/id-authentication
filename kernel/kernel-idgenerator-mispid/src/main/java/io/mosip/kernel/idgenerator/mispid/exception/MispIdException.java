package io.mosip.kernel.idgenerator.mispid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for MispIdException.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MispIdException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 9138117160521928565L;

	/**
	 * Constructor for MispIdException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause.
	 */
	public MispIdException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
