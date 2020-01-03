package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class AvailabilityTableNotAccessableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	private MainResponseDTO<?> mainResponseDTO;

	public AvailabilityTableNotAccessableException(String msg) {
		super("", msg);
	}

	public AvailabilityTableNotAccessableException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AvailabilityTableNotAccessableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AvailabilityTableNotAccessableException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}
	
	public AvailabilityTableNotAccessableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AvailabilityTableNotAccessableException() {
		super();
	}
}
