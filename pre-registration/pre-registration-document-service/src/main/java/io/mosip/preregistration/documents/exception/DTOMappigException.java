/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the DTOMappigException that occurs when the DTO Mapping fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class DTOMappigException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8143377803310016937L;

	/**
	 * Default constructor
	 */
	public DTOMappigException() {

	}

	/**
	 * @param message
	 */
	public DTOMappigException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DTOMappigException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_010.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 * @param message
	 * @param cause
	 */
	public DTOMappigException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 * @param message
	 */
	public DTOMappigException(String errorCode, String message) {
		super(errorCode, message);
	}
}
