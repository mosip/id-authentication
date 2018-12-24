package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

public class DTOMappigException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8143377803310016937L;

	public DTOMappigException() {

	}

	public DTOMappigException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message);
	}

	public DTOMappigException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message, cause);

	}

	public DTOMappigException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public DTOMappigException(String errorCode, String message) {
		super(errorCode, message);
	}
}

