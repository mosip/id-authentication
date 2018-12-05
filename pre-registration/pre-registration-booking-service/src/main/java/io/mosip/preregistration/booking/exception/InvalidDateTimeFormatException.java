package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class InvalidDateTimeFormatException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;

	public InvalidDateTimeFormatException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_008.toString(), message);
	}
}
