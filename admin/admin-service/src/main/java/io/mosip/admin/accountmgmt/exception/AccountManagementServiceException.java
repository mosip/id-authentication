package io.mosip.admin.accountmgmt.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


/**
 *  Class AccountManagementServiceException.
 *  @author Srinivasan
 *  @since 1.0.0
 */
public class AccountManagementServiceException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -769218645870355715L;
	
	/**
	 * Instantiates a new account management service exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public AccountManagementServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor that initializes exception.
	 *
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 * @param rootCause    the specified cause
	 */
	public AccountManagementServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}

