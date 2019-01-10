package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingTimeSlotNotSeletectedException extends BaseUncheckedException {

	private static final long serialVersionUID = -4543476272744645661L;

	public BookingTimeSlotNotSeletectedException(String msg) {
		super("", msg);
	}

	public BookingTimeSlotNotSeletectedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingTimeSlotNotSeletectedException() {
		super();
	}
}
