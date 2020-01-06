package io.mosip.kernel.idgenerator.rid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for inputs lengths.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

public class InputLengthException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842522173497867519L;

	public InputLengthException() {
		super();

	}

	public InputLengthException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public InputLengthException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
