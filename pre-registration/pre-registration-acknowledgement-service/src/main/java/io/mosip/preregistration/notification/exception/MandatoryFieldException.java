package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
public class MandatoryFieldException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1298682891599963309L;

	public MandatoryFieldException(String msg) {
		super("", msg);
	}

	public MandatoryFieldException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public MandatoryFieldException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public MandatoryFieldException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public MandatoryFieldException() {
		super();
	}
}
