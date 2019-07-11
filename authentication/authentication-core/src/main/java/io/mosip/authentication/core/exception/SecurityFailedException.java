package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class SecurityFailedException - Thrown when security check is failed.
 *
 * @author Manoj SP
 */
public class SecurityFailedException extends IdAuthenticationBusinessException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8285915287327896386L;
	
	/**
	 * Instantiates a new security failed exception.
	 */
	public SecurityFailedException() {
		super();
	}

	/**
	 * Instantiates a new security failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public SecurityFailedException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new security failed exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public SecurityFailedException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
