package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class AvailablityNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public AvailablityNotFoundException(String msg) {
		super("", msg);
	}

	public AvailablityNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AvailablityNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AvailablityNotFoundException(String errorCode, String errorMessage,MainResponseDTO<?> respone) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=respone;
	}

	public AvailablityNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AvailablityNotFoundException() {
		super();
	}
}
