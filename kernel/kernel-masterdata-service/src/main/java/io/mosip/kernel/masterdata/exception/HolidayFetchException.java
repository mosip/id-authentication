package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of error while fetching holiday is found
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 25-10-2018
 */
public class HolidayFetchException extends BaseUncheckedException {

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
	public HolidayFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
