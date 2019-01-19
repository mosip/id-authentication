package io.mosip.kernel.core.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when the JSON schema provided is null.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class NullJsonSchemaException extends BaseUncheckedException {

	private static final long serialVersionUID = -821123709407658107L;


	public NullJsonSchemaException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
