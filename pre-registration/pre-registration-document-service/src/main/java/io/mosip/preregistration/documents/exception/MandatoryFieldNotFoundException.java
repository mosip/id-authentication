package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

public class MandatoryFieldNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8143377803310016937L;

	public MandatoryFieldNotFoundException() {
		super();
	}

	public MandatoryFieldNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_014.toString(), message);
	}

	public MandatoryFieldNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_014.toString(), message, cause);

	}

	public MandatoryFieldNotFoundException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public MandatoryFieldNotFoundException(String errorCode, String message) {
		super(errorCode, message);
	}

}
