package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;

public class IdAuthenticationBaseException extends BaseCheckedException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1163954411549137376L;
	
	private String actionCode;

	public IdAuthenticationBaseException() {
		super();
	}

	public IdAuthenticationBaseException(String errorMessage) {
		super(errorMessage);
	}

	public IdAuthenticationBaseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public IdAuthenticationBaseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
	
	/**
	 * Constructs exception for the given {@code IdAuthenticationErrorConstants}.
	 *
	 * @param exceptionConstant the exception constant
	 * @see BaseUncheckedException#BaseUncheckedException(String, String)
	 */
	public IdAuthenticationBaseException(IdAuthenticationErrorConstants exceptionConstant) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
		this.actionCode = exceptionConstant.getActionCode();
	}

	/**
	 * Constructs exception for the given {@code IdAuthenticationErrorConstants} and {@code Throwable}.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 * @see BaseUncheckedException#BaseUncheckedException(String, String, Throwable)
	 */
	public IdAuthenticationBaseException(IdAuthenticationErrorConstants exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
		this.actionCode = exceptionConstant.getActionCode();
	}
	
	public String getActionCode() {
		return actionCode;
	}

}