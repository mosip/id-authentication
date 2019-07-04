package io.mosip.kernel.core.signatureutil.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 *  ParseResponseException class.
 *
 * @author Srinivasan
 */
public class ParseResponseException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3383837827871687253L;

	/**
	 * Instantiates a new parses the response exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 */
	public ParseResponseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * Instantiates a new parses the response exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public ParseResponseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
