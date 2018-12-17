package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class AppointmentBookingFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentBookingFailedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_005.toString(), message);
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentBookingFailedException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_005.toString(), errorMessage, rootCause);
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
