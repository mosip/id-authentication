package org.mosip.auth.core.exception;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;

/**
 * The Class UnknownException - Thrown when an unknown exception occurs.
 *
 * @author Manoj SP
 */
public class IDAuthenticationUnknownException extends IdAuthenticationBusinessException {

	private static final long serialVersionUID = 1970301146105673681L;

	/**
	 * Instantiates a new unknown exception.
	 */
	public IDAuthenticationUnknownException() {
		super();
	}
	
	/**
	 * Instantiates a new unknown exception using errorCode and errorMessage.
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public IDAuthenticationUnknownException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new unknown exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public IDAuthenticationUnknownException(IdAuthenticationErrorConstants exceptionConstant) {
		super(exceptionConstant);
	}

}
