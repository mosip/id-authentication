package io.mosip.idrepository.core.exception;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class IdRepoAppUncheckedException.
 *
 * @author Manoj SP
 */
public class IdRepoAppUncheckedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/**
	 * Instantiates a new id repo app unchecked exception.
	 */
	public IdRepoAppUncheckedException() {
		super();
	}

	/**
	 * Instantiates a new id repo app unchecked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public IdRepoAppUncheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app unchecked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
	 */
	public IdRepoAppUncheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo app unchecked exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdRepoAppUncheckedException(IdRepoErrorConstants exceptionConstant) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
	}

	/**
	 * Instantiates a new id repo app unchecked exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 */
	public IdRepoAppUncheckedException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
	}
}
