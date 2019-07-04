package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class AuthenticationFailedException - Exception thrown when Authentication of user is failed.
 *
 * @author Manoj SP
 */
public class AuthenticationFailedException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5573584039108669173L;
	
	/**
	 * Instantiates a new authentication failed exception.
	 */
	public AuthenticationFailedException() {
		super();
	}

	/**
	 * Instantiates a new authentication failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public AuthenticationFailedException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new authentication failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public AuthenticationFailedException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
