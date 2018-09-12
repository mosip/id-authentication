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
 * {@link Exception} to be thrown when pattern does not contain recommended
 * elements
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PatternSyntaxExeption extends BaseUncheckedException {

	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = 105555531L;

	/**
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public PatternSyntaxExeption(LogExeptionCodeConstants errorCode, LogExeptionCodeConstants errorMessage) {
		super(errorCode.getValue(), errorMessage.getValue());
	}

}
