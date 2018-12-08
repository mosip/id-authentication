package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingTimeSlotNotSeletectedException extends BaseUncheckedException {

	private static final long serialVersionUID = -4543476272744645661L;

	public BookingTimeSlotNotSeletectedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_003.toString(), message);
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingTimeSlotNotSeletectedException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_003.toString(), errorMessage, rootCause);
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
