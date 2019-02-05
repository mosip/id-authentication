/*
 * 
 * 
 */
package io.mosip.kernel.core.pinvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when Pin is invalid
 * 
 * @author Uday Kumar
 * @since 1.0.0
 */
public class InvalidPinException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1L;

	

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *             exception code
	 * @param errorMessage
	 *            exception message
	 */
	public InvalidPinException(String errorCode, String errorMessage) {

		super(errorCode, errorMessage);

	}
}
