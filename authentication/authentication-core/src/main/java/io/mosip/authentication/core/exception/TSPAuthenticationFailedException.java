package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class TSPAuthenticationFailedException - Thrown when TSP authentication is failed.
 *
 * @author Manoj SP
 */
public class TSPAuthenticationFailedException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3377853712826029146L;
	
	/**
	 * Instantiates a new TSP authentication failed exception.
	 */
	public TSPAuthenticationFailedException() {
		super();
	}

	/**
	 * Instantiates a new TSP authentication failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public TSPAuthenticationFailedException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new TSP authentication failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public TSPAuthenticationFailedException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
