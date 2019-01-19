package io.mosip.kernel.core.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any IO interrupt while reading JSON schema file.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class FileIOException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = 9015684848934917563L;

	/**
	 * Constructor for FileIOException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param rootCause
	 * 			  root cause of exception.
	 */
	public FileIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
