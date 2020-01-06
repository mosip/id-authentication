package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class InvalidDateTimeFormatException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;
	private MainResponseDTO<?> mainResponseDTO; 

	public InvalidDateTimeFormatException(String msg) {
		super("", msg);
	}

	public InvalidDateTimeFormatException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public InvalidDateTimeFormatException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public InvalidDateTimeFormatException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public InvalidDateTimeFormatException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResponseDTO=response;
	} 

	public InvalidDateTimeFormatException() {
		super();
	}
}
