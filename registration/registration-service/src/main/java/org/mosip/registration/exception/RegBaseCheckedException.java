package org.mosip.registration.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The class to handle all the checked exception in REG
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegBaseCheckedException extends BaseCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1011167705842553033L;

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO: add log at debug level
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message
	 * @param throwable
	 *            the specified cause
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);
		// TODO: add log at debug level
	}
}
