package io.mosip.kernel.uingenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for handling different exceptions in the service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class UinGeneratorServiceException extends BaseUncheckedException {

	/**
	 * Generated Serialzed version ID.
	 */
	private static final long serialVersionUID = 7542790575912622884L;

	/**
	 * Constructor for the exception class with errorCode and errorMessage as the
	 * argument.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	public UinGeneratorServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
