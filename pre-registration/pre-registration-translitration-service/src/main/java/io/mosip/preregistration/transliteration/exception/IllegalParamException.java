/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the IllegalParamException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class IllegalParamException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6810058264320216283L;
	
	/**
	 * @param msg
	 */
	public IllegalParamException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public IllegalParamException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public IllegalParamException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
