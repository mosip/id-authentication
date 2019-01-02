/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentFailedToDeleteException
 * 
 * @author Tapaswini Behera
 * @since 1.0.0
 * 
 */
public class DocumentFailedToDeleteException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructore
	 */
	public DocumentFailedToDeleteException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public DocumentFailedToDeleteException(String errorMessage) {
		super(ErrorCodes.PRG_PAM_DOC_015.toString(), errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public DocumentFailedToDeleteException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public DocumentFailedToDeleteException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_PAM_DOC_015.toString(), errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public DocumentFailedToDeleteException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
