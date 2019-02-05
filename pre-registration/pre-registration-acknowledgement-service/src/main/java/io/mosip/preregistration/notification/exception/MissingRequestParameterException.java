/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the MissingRequestParameterException
 * 
 * @author  Sanober Noor
 * @since 1.0.0
 * 
 */
public class MissingRequestParameterException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public MissingRequestParameterException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public MissingRequestParameterException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public MissingRequestParameterException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
}
