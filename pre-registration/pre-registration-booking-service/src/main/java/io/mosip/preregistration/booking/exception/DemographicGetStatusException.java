package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class DemographicGetStatusException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DemographicGetStatusException(String msg) {
		super(ErrorCodes.PRG_BOOK_RCI_012.toString(), msg);
	}

	public DemographicGetStatusException(String msg, Throwable cause) {
		super(ErrorCodes.PRG_BOOK_RCI_012.toString(), msg, cause);
	}

	public DemographicGetStatusException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public DemographicGetStatusException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
