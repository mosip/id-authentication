package io.mosip.preregistration.auth.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class provides custom Exception for send otp failed scenario
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
public class SendOtpFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public SendOtpFailedException(String errorCode, String errorMessage) {
		super(errorCode,errorMessage);
	}
	 
}
