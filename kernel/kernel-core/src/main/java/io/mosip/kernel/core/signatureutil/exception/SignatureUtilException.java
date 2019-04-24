package io.mosip.kernel.core.signatureutil.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


/**
 * The Class SignatureUtilException.
 * @author Srinivasan
 */
public class SignatureUtilException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6291038762313595129L;

	/**
	 * Instantiates a new signature util exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public SignatureUtilException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		
	}

	/**
	 * Instantiates a new signature util exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public SignatureUtilException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}
}
