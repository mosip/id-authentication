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
 * {@link Exception} to be thrown when implementation is not found
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class ImplementationNotFound extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 105555533L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public ImplementationNotFound(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
