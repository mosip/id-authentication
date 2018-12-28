/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentFailedToDeleteException that occurs when the document deletion fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentFailedToDeleteException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;

	/**
	 * Default constructor
	 */
	public DocumentFailedToDeleteException() {
		super();
	}

	/**
	 * @param message
	 */
	public DocumentFailedToDeleteException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_011.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentFailedToDeleteException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_011.toString(), message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public DocumentFailedToDeleteException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

}
