package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class BookingTimeSlotNotSeletectedException extends BaseUncheckedException {

	private static final long serialVersionUID = -4543476272744645661L;
	private MainResponseDTO<?> mainResponseDTO;

	public BookingTimeSlotNotSeletectedException(String msg) {
		super("", msg);
	}

	public BookingTimeSlotNotSeletectedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public BookingTimeSlotNotSeletectedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingTimeSlotNotSeletectedException() {
		super();
	}
}
