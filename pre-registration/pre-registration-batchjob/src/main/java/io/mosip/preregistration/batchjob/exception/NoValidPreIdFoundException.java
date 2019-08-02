package io.mosip.preregistration.batchjob.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class NoValidPreIdFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6469856598814217956L;
	private MainResponseDTO<?> response;
	public NoValidPreIdFoundException(String message) {
		super("",message);
	}
	public NoValidPreIdFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoValidPreIdFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public NoValidPreIdFoundException(String errorCode, String errorMessage,MainResponseDTO<?> responseDTO) {
		super(errorCode, errorMessage);
		this.response=responseDTO;
	}
}

