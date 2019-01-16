package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class AppointmentBookingFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentBookingFailedException(String msg) {
		super("", msg);
	}

	public AppointmentBookingFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentBookingFailedException() {
		super();
	}
}
