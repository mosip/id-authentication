/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;


/**
 * 
 * This class defines the LoginServiceException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class LoginServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ServiceError> validationErrorList;
	

	public List<ServiceError> getValidationErrorList() {
		return validationErrorList;
	}

	public LoginServiceException(List<ServiceError> validationErrorList) {
		this.validationErrorList = validationErrorList;
	}

}
