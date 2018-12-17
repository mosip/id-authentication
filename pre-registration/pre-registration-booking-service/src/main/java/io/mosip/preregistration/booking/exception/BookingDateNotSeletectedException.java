package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingDateNotSeletectedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090038321393006576L;

	public BookingDateNotSeletectedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_008.toString(), message);
	}

	public BookingDateNotSeletectedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingDateNotSeletectedException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_008.toString(), errorMessage, rootCause);
	}

	public BookingDateNotSeletectedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
