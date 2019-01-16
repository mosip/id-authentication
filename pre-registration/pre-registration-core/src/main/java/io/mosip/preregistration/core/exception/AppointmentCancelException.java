package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Kishan Rathore
 *
 */
public class AppointmentCancelException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8898932631567778670L;
	
	public AppointmentCancelException(String msg) {
		super("", msg);
	}

	public AppointmentCancelException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentCancelException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentCancelException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentCancelException() {
		super();
	}

}
