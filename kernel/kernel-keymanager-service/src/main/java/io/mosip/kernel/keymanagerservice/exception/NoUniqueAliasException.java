package io.mosip.kernel.keymanagerservice.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of applicationId is not present
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class NoUniqueAliasException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 8621530697947108810L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NoUniqueAliasException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
