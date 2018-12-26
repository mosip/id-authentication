/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentVirusScanException that occurs when the virus
 * scan for a document fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentVirusScanException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8143377803310016937L;

	/**
	 * Default constructor
	 */
	public DocumentVirusScanException() {

	}

	/**
	 * @param message
	 */
	public DocumentVirusScanException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentVirusScanException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public DocumentVirusScanException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public DocumentVirusScanException(String errorCode, String message) {
		super(errorCode, message);
	}
}
