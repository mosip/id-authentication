package io.mosip.kernel.core.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any IO interrupt while reading input JSON Schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class JsonSchemaIOException extends BaseCheckedException {

	private static final long serialVersionUID = 6632617381107602675L;


	public JsonSchemaIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
