package io.mosip.preregistration.auth.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class provides custom Exception for calidation fail scenario
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
public class UserIdOtpFaliedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public UserIdOtpFaliedException(String errorCode,String errorMessage) {
		super(errorCode,errorMessage);
	}

}
