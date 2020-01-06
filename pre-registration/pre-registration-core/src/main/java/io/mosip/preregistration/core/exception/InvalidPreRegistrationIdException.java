package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class InvalidPreRegistrationIdException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898906527162403384L;

	public InvalidPreRegistrationIdException(String errCode, String errMessage) {
		super(errCode, errMessage);
	}
}
