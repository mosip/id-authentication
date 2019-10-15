package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class RestCallException extends BaseUncheckedException{

	private static final long serialVersionUID = 1L;
	   private MainResponseDTO<?> mainresponseDTO;

	/**
	 * Default constructor
	 */
	public RestCallException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public RestCallException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public RestCallException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainresponseDTO=response;
	}

	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RestCallException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	
	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RestCallException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
