package org.mosip.auth.core.exception;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

/**
 * Thrown when an exception occurs using Rest exchange.
 * 
 * @author Manoj SP
 *
 */
public class RestServiceException extends IdAuthenticationAppException {

	private static final long serialVersionUID = 372518972095526748L;
	
	public RestServiceException() {
		super();
	}

	/**
	 * Instantiates a new rest client exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new rest client exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public RestServiceException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
