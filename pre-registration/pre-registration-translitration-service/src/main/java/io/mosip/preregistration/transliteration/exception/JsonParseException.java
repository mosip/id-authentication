/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the JsonParseException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class JsonParseException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	public JsonParseException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public JsonParseException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public JsonParseException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
