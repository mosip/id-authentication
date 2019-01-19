package io.mosip.kernel.core.idrepo.exception;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * The Class IdRepoUnknownException.
 *
 * @author Manoj SP
 */
public class IdRepoUnknownException extends IdRepoAppException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4349577830351654726L;

	/**
	 * Instantiates a new id repo unknown exception.
	 */
	public IdRepoUnknownException() {
		super();
	}

	/**
	 * Instantiates a new id repo unknown exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public IdRepoUnknownException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo unknown exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public IdRepoUnknownException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo unknown exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdRepoUnknownException(IdRepoErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new id repo unknown exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdRepoUnknownException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
