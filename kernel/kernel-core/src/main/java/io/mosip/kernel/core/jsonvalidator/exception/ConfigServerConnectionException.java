package io.mosip.kernel.core.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when there is any interruption while connecting to config Server
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class ConfigServerConnectionException extends BaseUncheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = -1314921055887937322L;


	/**
	 * Constructor for ConfigServerConnectionException class.
	 * 
	 * @param errorCode
	 *            the error code of the exception.
	 * @param errorMessage
	 *            the error message associated with the exception.
	 */
	public ConfigServerConnectionException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
