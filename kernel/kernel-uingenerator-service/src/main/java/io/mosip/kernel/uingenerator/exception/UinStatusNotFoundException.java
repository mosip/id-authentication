package io.mosip.kernel.uingenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for Uin Not Found Exception
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public class UinStatusNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1187679423065681944L;

	/**
	 * Constructor the initialize UinStatusNotFoundException
	 * 
	 * @param errorCode    The errorcode for this exception
	 * @param errorMessage The error message for this exception
	 */
	public UinStatusNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
