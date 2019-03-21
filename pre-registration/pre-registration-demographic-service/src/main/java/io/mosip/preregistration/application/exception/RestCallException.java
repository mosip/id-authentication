package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RestCallException extends BaseUncheckedException{

	private static final long serialVersionUID = 1L;

	/**
	 * @param msg pass the error message
	 */
	public RestCallException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 */
	public RestCallException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 * @param cause pass the cause
	 */
	public RestCallException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
}
