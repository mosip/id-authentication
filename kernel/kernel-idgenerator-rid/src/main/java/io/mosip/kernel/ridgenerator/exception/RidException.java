package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * rid exception
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public class RidException extends BaseUncheckedException {

	/**
	 * generated rid exception
	 */
	private static final long serialVersionUID = 4207046836454691003L;

	public RidException() {
		super();
	}

	public RidException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public RidException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public RidException(String errorMessage) {
		super(errorMessage);
	}

}
