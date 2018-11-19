/*
 * 
 * 
 */
package io.mosip.kernel.core.datavalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when Phone number is invalid
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class InvalidPhoneNumberException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -3556229489431119187L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public InvalidPhoneNumberException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
