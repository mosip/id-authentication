package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class IncorrectIDException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898906527162403384L;

	public IncorrectIDException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_006.toString(), message);
	}
}
