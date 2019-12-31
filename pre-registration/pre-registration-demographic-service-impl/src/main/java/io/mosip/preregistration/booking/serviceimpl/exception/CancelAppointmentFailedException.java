package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class CancelAppointmentFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public CancelAppointmentFailedException(String msg) {
		super("", msg);
	}

	public CancelAppointmentFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public CancelAppointmentFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public CancelAppointmentFailedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public CancelAppointmentFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public CancelAppointmentFailedException() {
		super();
	}
}
