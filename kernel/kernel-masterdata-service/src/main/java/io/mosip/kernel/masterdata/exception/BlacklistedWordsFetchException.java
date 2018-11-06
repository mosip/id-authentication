package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class thrown exception when not able to fetch blacklisted
 * words
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
public class BlacklistedWordsFetchException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -3602594448738503854L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public BlacklistedWordsFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
