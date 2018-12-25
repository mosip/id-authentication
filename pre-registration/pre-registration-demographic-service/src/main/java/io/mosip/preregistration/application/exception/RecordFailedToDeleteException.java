/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the RecordFailedToDeleteException
 * 
 * @author RecordNotFoundException
 * @since 1.0.0
 * 
 */
public class RecordFailedToDeleteException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public RecordFailedToDeleteException() {
		super();
	}

	/**
	 * @param errorMessage
	 */
	public RecordFailedToDeleteException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public RecordFailedToDeleteException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage
	 * @param rootCause
	 */
	public RecordFailedToDeleteException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public RecordFailedToDeleteException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
