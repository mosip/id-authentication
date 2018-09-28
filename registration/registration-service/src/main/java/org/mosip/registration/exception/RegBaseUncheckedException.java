package org.mosip.registration.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class for handling the REG unchecked exception
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegBaseUncheckedException extends BaseUncheckedException {

	/**
	 * Serializable Version Id
	 */
	private static final long serialVersionUID = 2308455130154511324L;

	/**
	 * Constructor
	 * 
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 */
	public RegBaseUncheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO: Add error log
	}

	/**
	 * Constructor
	 * 
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 * @param throwable
	 *            the Cause of exception
	 */
	public RegBaseUncheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);
		// TODO: Add error log
	}
}
