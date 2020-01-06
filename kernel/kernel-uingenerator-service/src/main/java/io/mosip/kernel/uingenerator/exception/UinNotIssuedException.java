package io.mosip.kernel.uingenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for Uin Not Found Exception
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public class UinNotIssuedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3073371775187895432L;

	/**
	 * Constructor the initialize UinNotIssuedException
	 * 
	 * @param errorCode    The errorcode for this exception
	 * @param errorMessage The error message for this exception
	 */
	public UinNotIssuedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}
}
