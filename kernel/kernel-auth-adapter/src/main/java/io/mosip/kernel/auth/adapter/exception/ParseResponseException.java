package io.mosip.kernel.auth.adapter.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class ParseResponseException extends BaseUncheckedException {

	/**
	 * Serial Version Id
	 */
	private static final long serialVersionUID = 3383837827871687253L;

	public ParseResponseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public ParseResponseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
