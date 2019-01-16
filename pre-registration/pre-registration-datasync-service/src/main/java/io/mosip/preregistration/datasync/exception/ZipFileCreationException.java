package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * ZipFileCreationException
 * 
 * @author M1046129
 *
 */
public class ZipFileCreationException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public ZipFileCreationException() {
		super();
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 */
	public ZipFileCreationException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 */
	public ZipFileCreationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public ZipFileCreationException(String errorMessage, Throwable rootCause) {
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
	public ZipFileCreationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
