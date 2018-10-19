package org.mosip.demo.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception class in case of Invalid request
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class InvalidRequestException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 8621530697947108810L;

	/**
	 * Constructor to initialize the Invalid request exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public InvalidRequestException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
