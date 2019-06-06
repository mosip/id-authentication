package io.mosip.kernel.core.keymanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for NoSuchSecurityProviderException
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class KeystoreProcessingException extends BaseUncheckedException {
	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor initialize NoSuchSecurityProviderException
	 * 
	 * @param errorCode    The errorcode for this exception
	 * @param errorMessage The error message for this exception
	 */
	public KeystoreProcessingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * @param errorCode    The errorcode for this exception
	 * @param errorMessage The error message for this exception
	 * @param rootCause cause of the error occurred
	 */
	public KeystoreProcessingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
	
	
}
