package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class AppointmentCannotBeCanceledException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentCannotBeCanceledException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_001.toString(), message);
	}

	public AppointmentCannotBeCanceledException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AppointmentCannotBeCanceledException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_001.toString(), errorMessage, rootCause);
	}

	public AppointmentCannotBeCanceledException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentCannotBeCanceledException() {
		super();
	}
}
