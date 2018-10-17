/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.logger.logback.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.logger.logback.constant.LogExeptionCodeConstants;

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
	public PatternSyntaxException(LogExeptionCodeConstants errorCode,
			LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
