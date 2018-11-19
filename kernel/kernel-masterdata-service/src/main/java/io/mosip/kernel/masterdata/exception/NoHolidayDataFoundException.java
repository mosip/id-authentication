package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case fetching specific holiday by id found
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 25-10-2018
 */
public class NoHolidayDataFoundException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 3898366809981400493L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NoHolidayDataFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
