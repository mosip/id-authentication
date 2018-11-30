package io.kernel.idrepo.exception;

import io.kernel.idrepo.constant.IdRepoErrorConstants;

/**
 * The Class IdRepoDataValidationException.
 *
 * @author Manoj SP
 */
public class IdRepoDataValidationException extends IdRepoAppException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -637919650941847283L;

	/**
	 * Instantiates a new id repo data validation exception.
	 */
	public IdRepoDataValidationException() {
		super();
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public IdRepoDataValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public IdRepoDataValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdRepoDataValidationException(IdRepoErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdRepoDataValidationException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
