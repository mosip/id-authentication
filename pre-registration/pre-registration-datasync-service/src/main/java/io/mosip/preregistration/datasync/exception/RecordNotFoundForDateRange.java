package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * RecordNotFoundForDateRange Exception
 * 
 * @author M1043226
 *
 */
public class RecordNotFoundForDateRange extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public RecordNotFoundForDateRange() {
		super();
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 */
	public RecordNotFoundForDateRange(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 */
	public RecordNotFoundForDateRange(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public RecordNotFoundForDateRange(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public RecordNotFoundForDateRange(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}