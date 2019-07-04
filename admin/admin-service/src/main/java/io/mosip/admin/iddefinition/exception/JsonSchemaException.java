package io.mosip.admin.iddefinition.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Json Schema Exception
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public class JsonSchemaException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2063606367481599576L;

	public JsonSchemaException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

	public JsonSchemaException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
