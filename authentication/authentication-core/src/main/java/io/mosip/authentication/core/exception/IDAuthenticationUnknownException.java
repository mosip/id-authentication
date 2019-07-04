package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class UnknownException - Thrown when an unknown exception occurs.
 *
 * @author Manoj SP
 */
public class IDAuthenticationUnknownException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1970301146105673681L;

	/**
	 * Instantiates a new unknown exception.
	 */
	public IDAuthenticationUnknownException() {
		super();
	}

	/**
	 * Instantiates a new unknown exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IDAuthenticationUnknownException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}
	
	/**
	 * Instantiates a new unknown exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IDAuthenticationUnknownException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
