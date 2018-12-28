/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the ParsingException that occurs when the parsing fails
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 * 
 */
public class ParsingException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public ParsingException() {

	}

	/**
	 * @param message
	 */
	public ParsingException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParsingException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public ParsingException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public ParsingException(String errorCode, String message) {
		super(errorCode, message);
	}

}
