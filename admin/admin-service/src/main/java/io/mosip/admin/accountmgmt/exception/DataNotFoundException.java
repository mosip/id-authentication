package io.mosip.admin.accountmgmt.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class DataNotFoundException.
 */
public class DataNotFoundException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -769218645870355715L;

	/**
	 * Instantiates a new data not found exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public DataNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new data not found exception.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the root cause
	 */
	public DataNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
