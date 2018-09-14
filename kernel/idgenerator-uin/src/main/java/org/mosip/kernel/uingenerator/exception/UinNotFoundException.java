package org.mosip.kernel.uingenerator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

public class UinNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4878056037301351184L;

	/**
	 * Constructor the initialize UinNotFoundException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public UinNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
