package io.mosip.kernel.smsnotifier.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This Exception class handle empty Json responce.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class JsonParseException extends BaseUncheckedException {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 8161085174042890973L;

	/**
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause of exception.
	 */
	public JsonParseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
