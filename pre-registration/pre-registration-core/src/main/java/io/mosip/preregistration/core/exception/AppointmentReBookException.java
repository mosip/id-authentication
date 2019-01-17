package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Kishan Rathore
 *
 */
public class AppointmentReBookException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentReBookException(String msg) {
		super("", msg);
	}

	public AppointmentReBookException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentReBookException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentReBookException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentReBookException() {
		super();
	}
}
