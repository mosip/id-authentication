package io.mosip.kernel.otpnotification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class OtpNotifierServiceException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3383837827871687253L;

	public OtpNotifierServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		
	}

	public OtpNotifierServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

}
