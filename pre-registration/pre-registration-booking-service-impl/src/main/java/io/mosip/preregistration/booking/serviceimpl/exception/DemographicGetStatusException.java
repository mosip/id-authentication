package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class DemographicGetStatusException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResponseDTO;

	public DemographicGetStatusException(String msg) {
		super("", msg);
	}

	public DemographicGetStatusException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public DemographicGetStatusException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public DemographicGetStatusException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public DemographicGetStatusException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public DemographicGetStatusException() {
		super();
	}

}
