package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class AppointmentBookingFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public AppointmentBookingFailedException(String msg) {
		super("", msg);
	}

	public AppointmentBookingFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AppointmentBookingFailedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public AppointmentBookingFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentBookingFailedException() {
		super();
	}
}
