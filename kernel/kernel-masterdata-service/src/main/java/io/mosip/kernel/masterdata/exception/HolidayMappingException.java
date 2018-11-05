package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * Custom exception in case mapping fails
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 25-10-2018
 */
public class HolidayMappingException extends BaseUncheckedException{

	/**
	 * generated  serialVersionUID
	 */
	private static final long serialVersionUID = 8910349505307579500L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public HolidayMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
