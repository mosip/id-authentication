package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class BookingRegistrationCenterIdNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -5462747645675773416L;
	private MainResponseDTO<?> mainResponseDTO;

	public BookingRegistrationCenterIdNotFoundException(String msg) {
		super("", msg);
	}

	public BookingRegistrationCenterIdNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public BookingRegistrationCenterIdNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public BookingRegistrationCenterIdNotFoundException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public BookingRegistrationCenterIdNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public BookingRegistrationCenterIdNotFoundException() {
		super();
	}
}
