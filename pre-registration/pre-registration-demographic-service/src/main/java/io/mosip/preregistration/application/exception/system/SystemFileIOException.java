/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the SystemFileIOException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
/**
 * @author M1046129
 *
 */
public class SystemFileIOException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public SystemFileIOException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public SystemFileIOException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public SystemFileIOException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
