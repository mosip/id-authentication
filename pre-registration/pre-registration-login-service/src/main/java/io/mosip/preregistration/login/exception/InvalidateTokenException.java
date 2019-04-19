package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class provides custom Exception for invalidate token failed scenario
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
public class InvalidateTokenException extends BaseUncheckedException {

private static final long serialVersionUID = 1L;
	
	public InvalidateTokenException(String errorCode, String errorMessage) {
		super(errorCode,errorMessage);
	}
}
