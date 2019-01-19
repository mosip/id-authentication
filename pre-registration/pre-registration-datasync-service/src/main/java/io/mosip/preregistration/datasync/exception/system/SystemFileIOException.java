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
	 * Default constructor
	 */
	public SystemFileIOException() {
		super();
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 */
	public SystemFileIOException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 */
	public SystemFileIOException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public SystemFileIOException(String errorMessage, Throwable rootCause) {
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
	public SystemFileIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
