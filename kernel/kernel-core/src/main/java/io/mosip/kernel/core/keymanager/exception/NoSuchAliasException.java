package io.mosip.kernel.core.keymanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for NoSuchAliasException
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class NoSuchAliasException extends BaseUncheckedException {
	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor initialize NoSuchAliasException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NoSuchAliasException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
