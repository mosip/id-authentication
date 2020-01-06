package io.mosip.preregistration.notification.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class NotificationSeriveException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8039562312343751179L;

	private List<ServiceError> validationErrorList;
	private MainResponseDTO<?> mainResponseDTO;

	public List<ServiceError> getValidationErrorList() {
		return validationErrorList;
	}

	public NotificationSeriveException(List<ServiceError> validationErrorList, MainResponseDTO<?> response) {
		this.validationErrorList = validationErrorList;
		this.mainResponseDTO = response;
	}

	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResponseDTO;
	}

	public NotificationSeriveException() {
		super();
	}

	public NotificationSeriveException(String arg0, String arg1, Throwable arg2, MainResponseDTO<?> response) {
		super(arg0, arg1, arg2);
		this.mainResponseDTO = response;
	}

	public NotificationSeriveException(String errorCode, String errorMessage, MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDTO = response;
	}

	public NotificationSeriveException(String errorMessage, MainResponseDTO<?> response) {
		super(errorMessage);
		this.mainResponseDTO = response;
	}

}
