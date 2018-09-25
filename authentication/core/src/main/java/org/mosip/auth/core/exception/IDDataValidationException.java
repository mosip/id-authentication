package org.mosip.auth.core.exception;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class IDDataValidationException - Thrown when any Data validation error occurs.
 *
 * @author Manoj SP
 */
public class IDDataValidationException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7248433575478299970L;
	
	/**
	 * Instantiates a new ID data validation exception.
	 */
	public IDDataValidationException() {
		super();
	}

	/**
	 * Instantiates a new ID data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IDDataValidationException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new ID data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IDDataValidationException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
