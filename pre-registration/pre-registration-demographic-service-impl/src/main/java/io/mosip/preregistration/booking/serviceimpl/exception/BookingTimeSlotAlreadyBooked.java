package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class BookingTimeSlotAlreadyBooked extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7620811373366498697L;
	private MainResponseDTO<?> mainResponseDTO;

	public BookingTimeSlotAlreadyBooked(String msg) {
		super("", msg);
	}

	public BookingTimeSlotAlreadyBooked(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingTimeSlotAlreadyBooked(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public BookingTimeSlotAlreadyBooked(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public BookingTimeSlotAlreadyBooked(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingTimeSlotAlreadyBooked() {
		super();
	}
}
