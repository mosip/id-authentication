package io.mosip.preregistration.documents.exception;


import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * InvalidConnectionParameter Exception occurs when
 * connection is attempted with wrong credentials
 *
 */
public class InvalidConnectionParameters extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public InvalidConnectionParameters() {
		super();
	}

	public InvalidConnectionParameters(String message) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), message);
	}

	public InvalidConnectionParameters(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), message, cause);

	}

	public InvalidConnectionParameters(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public InvalidConnectionParameters(String errorCode, String message) {
		super(errorCode, message);
	}


}
