package io.mosip.kernel.uingenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for Uin Generation Job Exception
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class UinGenerationJobException extends BaseUncheckedException {

	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = -4878056037301351184L;

	/**
	 * Constructor the initialize UinGenerationJobException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public UinGenerationJobException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
