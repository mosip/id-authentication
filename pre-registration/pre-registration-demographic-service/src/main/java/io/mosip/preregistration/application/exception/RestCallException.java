package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RestCallException extends BaseUncheckedException{

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public RestCallException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public RestCallException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RestCallException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RestCallException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
