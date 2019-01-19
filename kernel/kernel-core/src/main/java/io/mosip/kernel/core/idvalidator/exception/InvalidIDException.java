/*
 * 
 * 
 */
package io.mosip.kernel.core.idvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when ID is invalid
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class InvalidIDException extends BaseUncheckedException {

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
	public InvalidIDException(String errorCode, String errorMessage) {

		super(errorCode, errorMessage);

	}
}
