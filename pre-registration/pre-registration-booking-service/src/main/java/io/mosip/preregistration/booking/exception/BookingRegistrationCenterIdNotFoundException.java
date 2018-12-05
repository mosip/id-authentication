package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class BookingRegistrationCenterIdNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -5462747645675773416L;

	public BookingRegistrationCenterIdNotFoundException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_004.toString(), message);
	}
}
