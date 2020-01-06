package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for empty inputs.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

public class EmptyInputException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842524563494167519L;

	public EmptyInputException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
