package io.mosip.kernel.otpmanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for Otp IO exception.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class OtpIOException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8462152397501555128L;

	/**
	 * Constructor for OtpIOException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the root cause.
	 */
	public OtpIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
