package io.mosip.preregistration.core.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author M1046129
 *
 */

@Getter
@Setter
public class InvalidRequestParameterException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898906527162403384L;
	
	private MainResponseDTO<?> mainResponseDto;
	private List<ExceptionJSONInfoDTO> exptionList;
	
	public InvalidRequestParameterException() {
		super();
	}

	public InvalidRequestParameterException(String errCode, String errMessage,MainResponseDTO<?> response) {
		super(errCode, errMessage);
		this.mainResponseDto=response;
	}
	public InvalidRequestParameterException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResponseDto=response;
	}
	
	
	public InvalidRequestParameterException(List<ExceptionJSONInfoDTO> exptionList,MainResponseDTO<?> response) {
		this.mainResponseDto=response;
		this.exptionList=exptionList;
	}
}
