package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of error while fetching application details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class ApplicationFetchException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 4951092021246585362L;

	/**
	 * Constructor to initialize exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public ApplicationFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
