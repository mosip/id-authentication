package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any IO interrupt while reading JSON String.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class JsonIOException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = 795618868850353876L;

	/**
	 * Constructor for JsonIOException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param rootCause
	 * 			  root cause of exception.
	 */
	public JsonIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
