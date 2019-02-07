package io.mosip.kernel.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exceptions for invalid language ids.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class InvalidTransliterationException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -1429752953542214921L;

	/**
	 * Constructor for InvalidTransliterationException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause.
	 */
	public InvalidTransliterationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Constructor for InvalidTransliterationException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	public InvalidTransliterationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
