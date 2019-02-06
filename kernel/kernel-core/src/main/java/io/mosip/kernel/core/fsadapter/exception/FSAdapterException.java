package io.mosip.kernel.core.fsadapter.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom class for HDFSAdapterException
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class FSAdapterException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5074628123959874252L;

	/**
	 * Constructor for HDFSAdapterException
	 * 
	 * @param errorCode
	 *            The errorcode
	 * @param errorMessage
	 *            The errormessage
	 * @param cause
	 *            The cause
	 */
	public FSAdapterException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}
	
	/**
	 * Constructor for HDFSAdapterException
	 * 
	 * @param errorCode
	 *            The errorcode
	 * @param errorMessage
	 *            The errormessage
	 */
	public FSAdapterException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
