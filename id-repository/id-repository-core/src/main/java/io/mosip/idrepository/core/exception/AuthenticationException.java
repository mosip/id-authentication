package io.mosip.idrepository.core.exception;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class AuthenticationException.
 *
 * @author Manoj SP
 */
public class AuthenticationException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;
	
	/** The status code. */
	private int statusCode;

	/**
	 * Instantiates a new authentication exception.
	 */
	public AuthenticationException() {
		super();
	}

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param statusCode the status code
	 */
	public AuthenticationException(String errorCode, String errorMessage, int statusCode) {
		super(errorCode, errorMessage);
		this.statusCode = statusCode;
	}

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 * @param statusCode the status code
	 */
	public AuthenticationException(String errorCode, String errorMessage, Throwable rootCause, int statusCode) {
		super(errorCode, errorMessage, rootCause);
		this.statusCode = statusCode;
	}

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param statusCode the status code
	 */
	public AuthenticationException(IdRepoErrorConstants exceptionConstant, int statusCode) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), statusCode);
		this.statusCode = statusCode;
	}

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 * @param statusCode the status code
	 */
	public AuthenticationException(IdRepoErrorConstants exceptionConstant, Throwable rootCause, int statusCode) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause, statusCode);
		this.statusCode = statusCode;
	}
	
	/**
	 * Gets the status code.
	 *
	 * @return the status code
	 */
	public int getStatusCode() {
		return statusCode;
	}
}
