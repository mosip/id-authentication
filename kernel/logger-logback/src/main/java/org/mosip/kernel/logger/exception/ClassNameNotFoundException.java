/*
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.logger.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.logger.constant.LogExeptionCodeConstants;

/**
 * {@link Exception} to be thrown when a class name is not found
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class ClassNameNotFoundException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 105555532L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public ClassNameNotFoundException(LogExeptionCodeConstants errorCode,
			LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
