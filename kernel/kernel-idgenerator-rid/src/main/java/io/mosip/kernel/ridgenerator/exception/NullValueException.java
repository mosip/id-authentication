package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class NullValueException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842522178894167519L;

	public NullValueException() {
		super();

	}

	public NullValueException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public NullValueException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
