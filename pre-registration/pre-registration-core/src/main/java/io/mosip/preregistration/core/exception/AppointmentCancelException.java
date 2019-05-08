package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Kishan Rathore
 *
 */

@Getter
public class AppointmentCancelException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8898932631567778670L;
	private MainResponseDTO<?> mainResponseDTO;
	
	public AppointmentCancelException(String msg) {
		super("", msg);
	}

	public AppointmentCancelException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AppointmentCancelException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AppointmentCancelException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public AppointmentCancelException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AppointmentCancelException() {
		super();
	}

}
