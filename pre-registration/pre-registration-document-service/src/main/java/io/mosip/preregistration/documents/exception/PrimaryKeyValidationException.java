package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

public class PrimaryKeyValidationException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;

	/**
	 * Default constructor
	 */
	public PrimaryKeyValidationException() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public PrimaryKeyValidationException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_021.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public PrimaryKeyValidationException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_021.toString(), message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public PrimaryKeyValidationException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public PrimaryKeyValidationException(String errorCode, String message) {
		super(errorCode, message);
	}

}
