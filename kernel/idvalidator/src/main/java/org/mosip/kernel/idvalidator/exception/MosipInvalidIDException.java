/*
 * 
 * 
 */
package org.mosip.kernel.idvalidator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.idvalidator.uinvalidator.constants.MosipIDExceptionCodeConstants;

/**
 * {@link Exception} to be thrown when ID is invalid
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class MosipInvalidIDException extends BaseUncheckedException {

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
	public MosipInvalidIDException(String errorCode,
			String errorMessage) {
       super(errorCode, errorMessage);
	}
}
