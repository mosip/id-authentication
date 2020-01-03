package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class AppointmentCannotBeCanceledException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public AppointmentCannotBeCanceledException(String msg) {
		super("", msg);
	}

	public AppointmentCannotBeCanceledException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentCannotBeCanceledException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AppointmentCannotBeCanceledException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public AppointmentCannotBeCanceledException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentCannotBeCanceledException() {
		super();
	}
}
