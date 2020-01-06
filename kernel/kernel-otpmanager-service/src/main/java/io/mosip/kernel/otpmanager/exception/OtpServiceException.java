package io.mosip.kernel.otpmanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class OtpServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6963954493840799987L;

	public OtpServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
