package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class InvalidDateTimeFormatException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;

	public InvalidDateTimeFormatException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_009.toString(), message);
	}

	public InvalidDateTimeFormatException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public InvalidDateTimeFormatException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_009.toString(), errorMessage, rootCause);
	}

	public InvalidDateTimeFormatException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public InvalidDateTimeFormatException() {
		super();
	}
}
