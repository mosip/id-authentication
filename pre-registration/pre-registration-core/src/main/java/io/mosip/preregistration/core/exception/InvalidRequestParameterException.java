package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class InvalidRequestParameterException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898906527162403384L;

	public InvalidRequestParameterException(String errCode, String errMessage) {
		super(errCode, errMessage);
	}
	public InvalidRequestParameterException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
