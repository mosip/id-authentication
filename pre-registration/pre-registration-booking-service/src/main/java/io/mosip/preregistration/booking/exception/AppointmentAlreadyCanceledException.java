package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class AppointmentAlreadyCanceledException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentAlreadyCanceledException(String msg) {
		super("", msg);
	}

	public AppointmentAlreadyCanceledException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentAlreadyCanceledException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentAlreadyCanceledException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentAlreadyCanceledException() {
		super();
	}
}
