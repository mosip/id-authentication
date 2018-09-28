/*
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.logger.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;

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
	public ImplementationNotFound(LogExeptionCodeConstants errorCode,
			LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
