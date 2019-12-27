package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class BookingPreIdNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8090038321393006576L;
	private MainResponseDTO<?> mainResponseDTO;
	
	public BookingPreIdNotFoundException(String msg) {
		super("", msg);
	}

	public BookingPreIdNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public BookingPreIdNotFoundException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public BookingPreIdNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingPreIdNotFoundException() {
		super();
	}
}
