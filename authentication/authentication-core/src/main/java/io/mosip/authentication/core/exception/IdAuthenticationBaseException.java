package io.mosip.authentication.core.exception;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The base exception for all checked exceptions used in ID Authentication
 * @author Loganathan Sekar
 *
 */
public class IdAuthenticationBaseException extends BaseCheckedException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1163954411549137376L;
	
	private String actionMessage;

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
		this.actionMessage= exceptionConstant.getActionMessage();
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
		this.actionMessage = exceptionConstant.getActionMessage();
	}
	
	public String getActionMessage() {
		return actionMessage;
	}

}