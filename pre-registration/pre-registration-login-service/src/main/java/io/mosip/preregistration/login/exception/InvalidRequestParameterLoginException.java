package io.mosip.preregistration.login.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.login.dto.MainResponseDTO;

public class InvalidRequestParameterLoginException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ExceptionJSONInfoDTO> validationErrorList;
	private MainResponseDTO<?> mainResposneDTO;

	public List<ExceptionJSONInfoDTO> getValidationErrorList() {
		return validationErrorList;
	}

	public InvalidRequestParameterLoginException(List<ExceptionJSONInfoDTO> validationErrorList,MainResponseDTO<?> response) {
		this.validationErrorList = validationErrorList;
		this.mainResposneDTO=response;
	}

	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResposneDTO;
	}	
	

}
