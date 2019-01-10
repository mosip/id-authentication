/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DocumentNotFoundException that occurs when document is
 * not found
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DocumentNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;

	/**
	 * Default constructor
	 */
	public DocumentNotFoundException() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public DocumentNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentNotFoundException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentNotFoundException(String errorCode, String message) {
		super(errorCode, message);
	}

}
