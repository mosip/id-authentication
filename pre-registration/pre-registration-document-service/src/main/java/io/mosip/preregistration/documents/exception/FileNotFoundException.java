/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the FileNotFoundException that occurs when requested
 * packet is not present in DFS
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class FileNotFoundException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public FileNotFoundException() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public FileNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public FileNotFoundException(String message, Throwable cause) {
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
	public FileNotFoundException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public FileNotFoundException(String errorCode, String message) {
		super(errorCode, message);
	}

}