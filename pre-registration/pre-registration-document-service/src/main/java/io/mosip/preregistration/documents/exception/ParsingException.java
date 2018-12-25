package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class ParsingException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public ParsingException() {

	}

	public ParsingException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message);
	}

	public ParsingException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message, cause);

	}

	public ParsingException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public ParsingException(String errorCode, String message) {
		super(errorCode, message);
	}

}
