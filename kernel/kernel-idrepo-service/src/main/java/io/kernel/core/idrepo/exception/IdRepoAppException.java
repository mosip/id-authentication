package io.kernel.core.idrepo.exception;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class IdRepoAppException.
 *
 * @author Manoj SP
 */
public class IdRepoAppException extends BaseCheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/** The id. */
	private String id;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 */
	public IdRepoAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public IdRepoAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public IdRepoAppException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param id the id
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, String id) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
		this.id = id;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 * @param id the id
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause, String id) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
		this.id = id;
	}

}
