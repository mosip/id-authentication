package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for inputs lengths.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

public class InputLengthException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842522173497867519L;

	public InputLengthException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
