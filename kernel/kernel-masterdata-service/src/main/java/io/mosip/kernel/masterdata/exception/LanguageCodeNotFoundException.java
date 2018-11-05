package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of Language code not found
 * 
 * @author Neha
 * @since 1.0.0
 *
 */

public class LanguageCodeNotFoundException extends BaseUncheckedException {

	/**
	 * Generated Serial Version ID
	 */
	private static final long serialVersionUID = 1989896527668537454L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 		The error code for this exception
	 * @param errorMessage
	 		The error message for this exception
	 */
	public LanguageCodeNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
