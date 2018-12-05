package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class BookingDateTimeNotSeletectedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090038321393006576L;

	public BookingDateTimeNotSeletectedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_005.toString(), message);
	}
}
