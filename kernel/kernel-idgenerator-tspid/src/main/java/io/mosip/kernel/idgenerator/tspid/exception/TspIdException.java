package io.mosip.kernel.idgenerator.tspid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for TspIdException.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class TspIdException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 9138117160521928565L;

	/**
	 * Constructor for TspIdException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause.
	 */
	public TspIdException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
