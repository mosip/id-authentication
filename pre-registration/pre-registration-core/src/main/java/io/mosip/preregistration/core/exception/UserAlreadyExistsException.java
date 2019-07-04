package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.exception.util.PreIssuanceExceptionCodes;

/**
 * UserAlreadyExists occurs when the user already registered
 *
 */
public class UserAlreadyExistsException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException() {
		super();
	}

	public UserAlreadyExistsException(String message) {
		super(PreIssuanceExceptionCodes.USER_ALREADY_EXIST, message);
	}

	public UserAlreadyExistsException(String message, Throwable cause) {
		super(PreIssuanceExceptionCodes.USER_ALREADY_EXIST, message, cause);
	}
}
