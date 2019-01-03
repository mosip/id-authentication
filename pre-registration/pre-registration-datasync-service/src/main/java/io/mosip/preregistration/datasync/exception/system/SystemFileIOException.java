/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.datasync.exception.system;

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
	 * @param msg  pass the error message
	 */
	public SystemFileIOException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public SystemFileIOException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public SystemFileIOException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
