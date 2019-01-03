/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the SystemUnsupportedEncodingException
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 * 
 */
public class SystemUnsupportedEncodingException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg  pass the error message
	 */
	public SystemUnsupportedEncodingException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public SystemUnsupportedEncodingException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public SystemUnsupportedEncodingException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
