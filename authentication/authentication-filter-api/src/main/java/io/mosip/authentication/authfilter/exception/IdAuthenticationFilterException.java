package io.mosip.authentication.authfilter.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;

/**
 * The Class IdAuthenticationFilterException.
 */
public class IdAuthenticationFilterException extends IDDataValidationException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8961194021036317286L;

	/**
	 * Instantiates a new id authentication filter exception.
	 */
	public IdAuthenticationFilterException() {
		super();
	}

	/**
	 * Instantiates a new id authentication filter exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public IdAuthenticationFilterException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id authentication filter exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public IdAuthenticationFilterException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	/**
	 * Instantiates a new id authentication filter exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdAuthenticationFilterException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	
	/**
	 * Instantiates a new id authentication filter exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdAuthenticationFilterException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}


}
