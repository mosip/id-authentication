package io.mosip.preregistration.auth.exceptions;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.preregistration.auth.dto.MainResponseDTO;

public class AuthServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ServiceError> validationErrorList;
	
	private MainResponseDTO<?> mainResposneDTO;

	public List<ServiceError> getValidationErrorList() {
		return validationErrorList;
	}

	public AuthServiceException(List<ServiceError> validationErrorList,MainResponseDTO<?> response) {
		this.validationErrorList = validationErrorList;
		this.mainResposneDTO=response;
	}

	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResposneDTO;
	}	
	
}
