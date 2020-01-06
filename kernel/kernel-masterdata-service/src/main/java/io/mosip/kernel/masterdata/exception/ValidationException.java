package io.mosip.kernel.masterdata.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Exception to hold validation errors
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public class ValidationException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 8764526395763989084L;

	private List<ServiceError> errors;

	public ValidationException(List<ServiceError> errors) {
		this.errors = errors;
	}

	public List<ServiceError> getErrors() {
		return errors;
	}

}
