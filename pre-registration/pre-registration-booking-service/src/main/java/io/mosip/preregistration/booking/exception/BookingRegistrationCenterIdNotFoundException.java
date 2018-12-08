package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingRegistrationCenterIdNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -5462747645675773416L;

	public BookingRegistrationCenterIdNotFoundException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_007.toString(), message);
	}

	public BookingRegistrationCenterIdNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingRegistrationCenterIdNotFoundException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_007.toString(), errorMessage, rootCause);
	}

	public BookingRegistrationCenterIdNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
