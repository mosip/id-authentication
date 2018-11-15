package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when the JSON object provided does not match with the respective Schema.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class UnidentifiedJsonException extends BaseUncheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = 43640357502304975L;
	
	/**
	 * Constructor for UnidentifiedJsonException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 */
	public UnidentifiedJsonException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
