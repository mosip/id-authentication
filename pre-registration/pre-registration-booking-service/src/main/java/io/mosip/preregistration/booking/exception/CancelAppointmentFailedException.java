package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class CancelAppointmentFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public CancelAppointmentFailedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_005.toString(), message);
	}

	public CancelAppointmentFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public CancelAppointmentFailedException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_005.toString(), errorMessage, rootCause);
	}

	public CancelAppointmentFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public CancelAppointmentFailedException() {
		super();
	}
}
