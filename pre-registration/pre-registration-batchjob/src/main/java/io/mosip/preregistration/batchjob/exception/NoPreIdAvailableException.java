/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class NoPreIdAvailableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8146722453630331685L;
	private MainResponseDTO<?> response;
	public NoPreIdAvailableException(String message) {
		super("",message);
	}
	public NoPreIdAvailableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoPreIdAvailableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
	
	public NoPreIdAvailableException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.response=response;
	}

}
