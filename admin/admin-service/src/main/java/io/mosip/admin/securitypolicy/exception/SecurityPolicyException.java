package io.mosip.admin.securitypolicy.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception for Security Policy Exception
 * 
 * @author Abhishek Kumar
 *
 */
public class SecurityPolicyException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -61665769442014559L;

	public SecurityPolicyException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public SecurityPolicyException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
