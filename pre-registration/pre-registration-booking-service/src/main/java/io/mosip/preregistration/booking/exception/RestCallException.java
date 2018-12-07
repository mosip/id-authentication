package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class RestCallException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RestCallException(String msg) {
		super("", msg);
	}

	public RestCallException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public RestCallException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
