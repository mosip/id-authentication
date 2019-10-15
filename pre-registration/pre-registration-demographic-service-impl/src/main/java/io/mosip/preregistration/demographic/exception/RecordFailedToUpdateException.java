/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the RecordFailedToDeleteException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
@Getter
public class RecordFailedToUpdateException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResponseDTO;

	/**
	 * Default constructor
	 */
	public RecordFailedToUpdateException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public RecordFailedToUpdateException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public RecordFailedToUpdateException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public RecordFailedToUpdateException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}
	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RecordFailedToUpdateException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public RecordFailedToUpdateException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
