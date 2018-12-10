package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingPreIdNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090038321393006576L;

	public BookingPreIdNotFoundException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_006.toString(), message);
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingPreIdNotFoundException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_006.toString(), errorMessage, rootCause);
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
