/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.document.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentFailedToUploadException that occurs when
 * document upload fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class InvalidDocumnetIdExcepion extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;

	/**
	 * Default constructor
	 */
	public InvalidDocumnetIdExcepion() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public InvalidDocumnetIdExcepion(String message) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public InvalidDocumnetIdExcepion(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(), message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public InvalidDocumnetIdExcepion(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public InvalidDocumnetIdExcepion(String errorCode, String message) {
		super(errorCode, message);
	}

}
