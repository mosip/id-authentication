/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * This class defines the ConnectionUnavailableException that occurs when the
 * connection is unavailable.
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class CephConnectionUnavailableException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public CephConnectionUnavailableException() {
		super();
	}

	/**
	 * @param message
	 *            pass Error Message
	 */
	public CephConnectionUnavailableException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_017.toString(), message);
	}

	/**
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass cause
	 */
	public CephConnectionUnavailableException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_017.toString(), message, cause);

	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public CephConnectionUnavailableException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public CephConnectionUnavailableException(String errorCode, String message) {
		super(errorCode, message);
	}
}
