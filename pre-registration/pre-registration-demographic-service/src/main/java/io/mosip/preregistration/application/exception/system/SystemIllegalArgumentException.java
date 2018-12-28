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
	 * @param msg
	 */
	public SystemIllegalArgumentException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public SystemIllegalArgumentException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public SystemIllegalArgumentException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
