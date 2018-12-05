package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class BookingTimeSlotNotSeletectedException extends BaseUncheckedException {

	private static final long serialVersionUID = -4543476272744645661L;

	public BookingTimeSlotNotSeletectedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_001.toString(), message);
	}
}
