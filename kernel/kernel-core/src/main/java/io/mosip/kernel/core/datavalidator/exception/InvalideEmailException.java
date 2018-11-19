/*
 * 
 * 
 */
package io.mosip.kernel.core.datavalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when Email is invalid
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class InvalideEmailException extends BaseUncheckedException {

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
	public InvalideEmailException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}