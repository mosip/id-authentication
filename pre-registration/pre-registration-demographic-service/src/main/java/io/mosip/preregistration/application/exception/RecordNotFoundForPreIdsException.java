/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the RecordNotFoundForPreIdsException
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 * 
 */
public class RecordNotFoundForPreIdsException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 *            pass the error message
	 */
	public RecordNotFoundForPreIdsException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 *            pass the error code
	 * @param msg
	 *            pass the error message
	 */
	public RecordNotFoundForPreIdsException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 *            pass the error code
	 * @param msg
	 *            pass the error message
	 * @param cause
	 *            pass the cause
	 */
	public RecordNotFoundForPreIdsException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
}
