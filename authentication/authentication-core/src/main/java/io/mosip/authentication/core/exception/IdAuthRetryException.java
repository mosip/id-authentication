package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class IdAuthRetryException - Unchecked exception used to trigger retry
 * in RestHelper.
 *
 * @author Manoj SP
 */
public class IdAuthRetryException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/**
	 * Instantiates a new id auth retry exception.
	 */
	public IdAuthRetryException() {
		super();
	}

	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public IdAuthRetryException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
	 */
	public IdAuthRetryException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdAuthRetryException(IdAuthenticationErrorConstants exceptionConstant) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
	}
	
	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdAuthRetryException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
	}

	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param rootCause the root cause
	 */
	public IdAuthRetryException(BaseCheckedException rootCause) {
		this(rootCause.getErrorCode(), rootCause.getErrorText(), rootCause);
	}

	/**
	 * Instantiates a new id auth retry exception.
	 *
	 * @param rootCause the root cause
	 */
	public IdAuthRetryException(BaseUncheckedException rootCause) {
		this(rootCause.getErrorCode(), rootCause.getErrorText(), rootCause);
	}
}
