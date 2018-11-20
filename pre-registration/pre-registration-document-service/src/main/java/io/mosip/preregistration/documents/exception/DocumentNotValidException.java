package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * DocumentNotValidException
 * 
 * @author M1043008
 *
 */
public class DocumentNotValidException extends BaseUncheckedException {

	/**
	 * Serial version Id
	 */
	private static final long serialVersionUID = 5252109871704396987L;

	public DocumentNotValidException() {
		super();

	}

	public DocumentNotValidException(String message) {
		super(ErrorCodes.PRG_PAM‌_004.toString(), message);

	}

	public DocumentNotValidException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM‌_004.toString(), message, cause);

	}

}
