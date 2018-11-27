package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when the Json String provided for validation is null.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class NullJsonNodeException extends BaseUncheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -1097192955195432642L;

	/**
	 * Constructor for NullJsonNodeException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param cause
	 * 	          cause of the error
	 */
	public NullJsonNodeException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
