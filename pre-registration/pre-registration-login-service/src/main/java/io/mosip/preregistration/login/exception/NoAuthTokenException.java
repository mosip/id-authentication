package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class NoAuthTokenException extends BaseUncheckedException{

private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;
	
	public NoAuthTokenException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode,errorMessage);
		this.mainResponseDto=response;
	}
}
