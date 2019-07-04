package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Kishan Rathore
 *
 */

@Getter
public class AppointmentBookException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public AppointmentBookException(String msg) {
		super("", msg);
	}

	public AppointmentBookException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentBookException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AppointmentBookException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public AppointmentBookException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentBookException() {
		super();
	}
}
