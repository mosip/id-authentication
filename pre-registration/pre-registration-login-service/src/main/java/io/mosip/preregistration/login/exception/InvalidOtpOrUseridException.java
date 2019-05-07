package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidOtpOrUseridException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;

	public InvalidOtpOrUseridException() {
		super();
		
	}

	public InvalidOtpOrUseridException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResponseDto=response;
		
	}

	public InvalidOtpOrUseridException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDto=response;
		
	}

	public InvalidOtpOrUseridException(String errorMessage,MainResponseDTO<?> response) {
		super(errorMessage);
		this.mainResponseDto=response;
		
	}
	
	
}

