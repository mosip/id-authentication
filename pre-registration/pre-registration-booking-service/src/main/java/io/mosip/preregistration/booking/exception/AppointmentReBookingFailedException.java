package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class AppointmentReBookingFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentReBookingFailedException(String msg) {
		super("", msg);
	}

	public AppointmentReBookingFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentReBookingFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentReBookingFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentReBookingFailedException() {
		super();
	}
}
