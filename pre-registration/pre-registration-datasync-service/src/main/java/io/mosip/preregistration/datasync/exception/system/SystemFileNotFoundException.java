/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.datasync.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the SystemIllegalArgumentException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class SystemFileNotFoundException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg pass the error message
	 */
	public SystemFileNotFoundException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public SystemFileNotFoundException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error Code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public SystemFileNotFoundException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
