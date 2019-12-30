package io.mosip.preregistration.demographic.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

public class IdValidationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> errorMessagesList;
	
	
	private MainResponseDTO<?> mainResposneDTO;

	public List<String> getErrorMessageList() {
		return errorMessagesList;
	}

	public IdValidationException(String errorCode,List<String> errrorList,MainResponseDTO<?> response) {
		super(errorCode, null);
		this.errorMessagesList = errrorList;
		this.mainResposneDTO=response;
	}

	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResposneDTO;
	}	
	

}
