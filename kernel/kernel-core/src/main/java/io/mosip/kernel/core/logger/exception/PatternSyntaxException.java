/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.logger.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be thrown when pattern does not contain recommended
 * elements
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PatternSyntaxException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 105555531L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public PatternSyntaxException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
