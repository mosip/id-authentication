package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.exception.util.PreIssuanceExceptionCodes;

/**
 * UserNameNotValidException occurs when the user name is not valid
 *
 */
public class UserNameNotValidException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public UserNameNotValidException() {
		super();
	}

	public UserNameNotValidException(String message) {
		super(PreIssuanceExceptionCodes.INVALID_USER_NAME, message);
	}

	public UserNameNotValidException(String message, Throwable cause) {
		super(PreIssuanceExceptionCodes.INVALID_USER_NAME, message, cause);
	}
}
