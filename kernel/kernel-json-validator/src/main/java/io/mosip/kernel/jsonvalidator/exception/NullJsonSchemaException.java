package io.mosip.kernel.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when the JSON schema provided is null.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class NullJsonSchemaException extends BaseUncheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -821123709407658107L;

	/**
	 * Constructor for NullJsonSchemaException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 * @param cause
	 * 	          cause of the error
	 */
	public NullJsonSchemaException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
