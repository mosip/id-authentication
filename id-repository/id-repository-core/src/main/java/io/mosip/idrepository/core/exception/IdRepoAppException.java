package io.mosip.idrepository.core.exception;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class IdRepoAppException.
 *
 * @author Manoj SP
 */
public class IdRepoAppException extends BaseCheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6748760277721155095L;

	/** The operation. */
	private String operation;

	/**
	 * Instantiates a new id repo app exception.
	 */
	public IdRepoAppException() {
		super();
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public IdRepoAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
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
	 * @param rootCause         the root cause
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param operation the operation
	 */
	public IdRepoAppException(String errorCode, String errorMessage, String operation) {
		super(errorCode, errorMessage);
		this.operation = operation;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
	 * @param operation the operation
	 */
	public IdRepoAppException(String errorCode, String errorMessage, Throwable rootCause, String operation) {
		super(errorCode, errorMessage, rootCause);
		this.operation = operation;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param operation the operation
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, String operation) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
		this.operation = operation;
	}

	/**
	 * Instantiates a new id repo app exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 * @param operation the operation
	 */
	public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause, String operation) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
		this.operation = operation;
	}

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

}
