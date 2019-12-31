package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class AvailabilityUpdationFailedException extends BaseUncheckedException{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8740121632422570371L;
	private MainResponseDTO<?> mainResponseDTO;
	
	public AvailabilityUpdationFailedException(String msg) {
		super("", msg);
	}

	public AvailabilityUpdationFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AvailabilityUpdationFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public AvailabilityUpdationFailedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public AvailabilityUpdationFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AvailabilityUpdationFailedException() {
		super();
	}
}
