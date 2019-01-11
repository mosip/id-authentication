package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingPreIdNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090038321393006576L;

	public BookingPreIdNotFoundException(String msg) {
		super("", msg);
	}

	public BookingPreIdNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingPreIdNotFoundException() {
		super();
	}
}
