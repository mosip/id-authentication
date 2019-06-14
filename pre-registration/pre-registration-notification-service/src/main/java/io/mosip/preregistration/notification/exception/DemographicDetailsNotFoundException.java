package io.mosip.preregistration.notification.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class DemographicDetailsNotFoundException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private final MainResponseDTO<?> mainResponseDTO;
	private List<ExceptionJSONInfoDTO> errorList;
	
	public List<ExceptionJSONInfoDTO> getErrorList() {
		return errorList;
	}

	
	public MainResponseDTO<?> getMainResposneDTO() {
		return mainResponseDTO;
	}
	/**
	 * @param msg
	 */
	public DemographicDetailsNotFoundException(List<ExceptionJSONInfoDTO> errorList,MainResponseDTO<?> response) {
		this.errorList=errorList;
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public DemographicDetailsNotFoundException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public DemographicDetailsNotFoundException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDTO=response;
	}
}


