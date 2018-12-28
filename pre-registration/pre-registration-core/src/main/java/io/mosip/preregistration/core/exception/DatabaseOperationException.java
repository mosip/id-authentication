package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.exception.util.PreIssuanceExceptionCodes;

public class DatabaseOperationException  extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public DatabaseOperationException() {
		super();
	}

	public DatabaseOperationException(String message) {
		super(PreIssuanceExceptionCodes.USER_INSERTION, message);
	}

	public DatabaseOperationException(String message, Throwable cause) {
		super(PreIssuanceExceptionCodes.USER_INSERTION, message, cause);
	}
}
