package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ConnectionUnavailableException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public ConnectionUnavailableException() {
		super();
	}

	public ConnectionUnavailableException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_017.toString(), message);
	}

	public ConnectionUnavailableException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_017.toString(), message, cause);

	}

	public ConnectionUnavailableException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public ConnectionUnavailableException(String errorCode, String message) {
		super(errorCode, message);
	}
}
