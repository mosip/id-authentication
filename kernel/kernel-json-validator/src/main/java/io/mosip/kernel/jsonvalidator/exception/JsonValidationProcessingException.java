package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Exception class when there is any interrupt in processing JSON validation.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class JsonValidationProcessingException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -3849227719514230853L;

	/**
	 * Constructor for JsonValidationProcessingException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 */
	public JsonValidationProcessingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
