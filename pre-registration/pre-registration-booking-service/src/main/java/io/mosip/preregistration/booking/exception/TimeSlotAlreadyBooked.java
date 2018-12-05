package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class TimeSlotAlreadyBooked extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7620811373366498697L;

	public TimeSlotAlreadyBooked(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_002.toString(), message);
	}
}
