/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the SystemIllegalArgumentException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class SystemIllegalArgumentException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg pass the error message
	 */
	public SystemIllegalArgumentException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public SystemIllegalArgumentException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error Code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public SystemIllegalArgumentException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
