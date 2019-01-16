package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Kishan Rathore
 *
 */
public class AppointmentBookException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentBookException(String msg) {
		super("", msg);
	}

	public AppointmentBookException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentBookException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentBookException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentBookException() {
		super();
	}
}
