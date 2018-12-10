package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class RecordNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;

	public RecordNotFoundException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_014.toString(), message);
	}

	public RecordNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public RecordNotFoundException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_014.toString(), errorMessage, rootCause);
	}

	public RecordNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
