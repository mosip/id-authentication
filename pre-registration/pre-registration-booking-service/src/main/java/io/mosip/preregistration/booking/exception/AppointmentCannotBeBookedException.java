package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class AppointmentCannotBeBookedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AppointmentCannotBeBookedException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_008.toString(), message);
	}
}
