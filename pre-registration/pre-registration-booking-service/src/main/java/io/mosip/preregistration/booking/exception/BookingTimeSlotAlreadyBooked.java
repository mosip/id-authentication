package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class BookingTimeSlotAlreadyBooked extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7620811373366498697L;

	public BookingTimeSlotAlreadyBooked(String msg) {
		super("", msg);
	}

	public BookingTimeSlotAlreadyBooked(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingTimeSlotAlreadyBooked(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public BookingTimeSlotAlreadyBooked(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingTimeSlotAlreadyBooked() {
		super();
	}
}
