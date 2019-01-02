/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the MandatoryFieldNotFound Exception that occurs when data
 * not pass to mandatory fields
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class MandatoryFieldNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8143377803310016937L;

	/**
	 * Default constructor
	 */
	public MandatoryFieldNotFoundException() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public MandatoryFieldNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_014.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public MandatoryFieldNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_014.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public MandatoryFieldNotFoundException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public MandatoryFieldNotFoundException(String errorCode, String message) {
		super(errorCode, message);
	}

}
