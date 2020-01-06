package io.mosip.preregistration.core.exception.util;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseResponseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;
	
	public ParseResponseException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDto=response;
		
	}

	public ParseResponseException(String errorCode, String errorMessage,Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage,rootCause);
		this.mainResponseDto=response;
		
	}


}
