/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the UnSupportedLanguageException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */
public class UnSupportedLanguageException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public UnSupportedLanguageException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public UnSupportedLanguageException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public UnSupportedLanguageException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
}
