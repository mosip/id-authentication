package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of error while fetching registration center is
 * found
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class RegistrationCenterFetchException extends BaseUncheckedException {

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
	public RegistrationCenterFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
