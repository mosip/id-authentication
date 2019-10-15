package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.demographic.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class BookingDeletionFailedException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	 private MainResponseDTO<?> mainresponseDTO;

	/**
	 * Default constructore
	 */
	public BookingDeletionFailedException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public BookingDeletionFailedException(String errorMessage) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public BookingDeletionFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public BookingDeletionFailedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainresponseDTO=response;
	}


	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public BookingDeletionFailedException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public BookingDeletionFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
