/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentFailedToUploadException that occurs when document upload fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentFailedToUploadException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;

	/**
	 * Default constructor
	 */
	public DocumentFailedToUploadException() {
		super();
	}

	/**
	 * @param message
	 */
	public DocumentFailedToUploadException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentFailedToUploadException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(), message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public DocumentFailedToUploadException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public DocumentFailedToUploadException(String errorCode, String message) {
		super(errorCode, message);
	}

}
