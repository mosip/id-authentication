/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the InvalidConnectionParameter Exception that occurs when
 * connection is attempted with wrong credentials
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class InvalidConnectionParameters extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public InvalidConnectionParameters() {
		super();
	}

	/**
	 * @param message
	 */
	public InvalidConnectionParameters(String message) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidConnectionParameters(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public InvalidConnectionParameters(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public InvalidConnectionParameters(String errorCode, String message) {
		super(errorCode, message);
	}

}
