package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class AvailabilityTableNotAccessableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AvailabilityTableNotAccessableException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_002.toString(), message);
	}

	public AvailabilityTableNotAccessableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AvailabilityTableNotAccessableException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_002.toString(), errorMessage, rootCause);
	}

	public AvailabilityTableNotAccessableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
