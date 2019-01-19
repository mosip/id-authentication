/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.logger.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * {@link Exception} to be file name is empty or null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class FileNameNotProvided extends BaseUncheckedException {

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
	public FileNameNotProvided(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
