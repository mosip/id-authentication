package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
@Getter
public class InvalidDateFormatException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7333914604834231072L;
	private MainResponseDTO<?> mainResponseDTO;

	public InvalidDateFormatException() {
		super();
	}

	public InvalidDateFormatException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public InvalidDateFormatException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public InvalidDateFormatException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDTO=response;
	}

	public InvalidDateFormatException(String errorMessage) {
		super(errorMessage);
	}
	

}
