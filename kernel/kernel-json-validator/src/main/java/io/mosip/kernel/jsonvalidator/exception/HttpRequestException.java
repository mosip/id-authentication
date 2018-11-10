package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when there is any interruption while reading schema from config server
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class HttpRequestException extends BaseUncheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -3969580195047347788L;

	/**
	 * Constructor for HttpRequestException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 */
	public HttpRequestException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
