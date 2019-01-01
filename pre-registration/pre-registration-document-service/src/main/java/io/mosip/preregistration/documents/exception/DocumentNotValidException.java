/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentNotValidException that occurs when document is
 * invalid
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentNotValidException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5252109871704396987L;

	/**
	 * Default constructor
	 */
	public DocumentNotValidException() {
		super();

	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public DocumentNotValidException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_004.toString(), message);

	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentNotValidException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_004.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentNotValidException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentNotValidException(String errorCode, String message) {
		super(errorCode, message);
	}

}
