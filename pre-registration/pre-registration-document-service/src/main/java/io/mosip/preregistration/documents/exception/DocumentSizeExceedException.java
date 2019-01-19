/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentSizeExceedException that occurs when the
 * document size exceeds the limit
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentSizeExceedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4879473387592007255L;

	/**
	 * Default constructor
	 */
	public DocumentSizeExceedException() {
		super();

	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentSizeExceedException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_007.toString(), message, cause);

	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public DocumentSizeExceedException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_007.toString(), message);

	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentSizeExceedException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentSizeExceedException(String errorCode, String message) {
		super(errorCode, message);
	}

}
