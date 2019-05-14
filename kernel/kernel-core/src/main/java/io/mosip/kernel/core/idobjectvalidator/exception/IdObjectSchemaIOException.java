package io.mosip.kernel.core.idobjectvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any IO interrupt while reading input
 * Schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class IdObjectSchemaIOException extends BaseCheckedException {

	private static final long serialVersionUID = 6632617381107602675L;

	public IdObjectSchemaIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
