package io.mosip.kernel.otpmanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exceptions for CRYPTO failure.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class CryptoFailureException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = -3069970234745966967L;

	/**
	 * Constructor for CryptoFailureException class.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause.
	 */
	public CryptoFailureException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
