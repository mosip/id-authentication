/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the JsonValidationException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class JsonValidationException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg  pass the error message
	 */
	public JsonValidationException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public JsonValidationException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public JsonValidationException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
