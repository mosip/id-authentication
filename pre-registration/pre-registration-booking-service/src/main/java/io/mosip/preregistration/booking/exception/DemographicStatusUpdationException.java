package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class DemographicStatusUpdationException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DemographicStatusUpdationException(String msg) {
		super(ErrorCodes.PRG_BOOK_RCI_011.toString(), msg);
	}

	public DemographicStatusUpdationException(String msg, Throwable cause) {
		super(ErrorCodes.PRG_BOOK_RCI_011.toString(), msg, cause);
	}

	public DemographicStatusUpdationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public DemographicStatusUpdationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
