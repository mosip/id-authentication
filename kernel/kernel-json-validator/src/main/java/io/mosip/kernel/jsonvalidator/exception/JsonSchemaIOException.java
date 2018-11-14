package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any IO interrupt while reading input JSON Schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class JsonSchemaIOException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = 6632617381107602675L;

	/**
	 * Constructor for JsonSchemaIOException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param rootCause
	 * 			  root cause of exception.
	 */
	public JsonSchemaIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
