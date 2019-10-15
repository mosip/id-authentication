/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.demographic.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentFailedToDeleteException
 * 
 * @author Tapaswini Behera
 * @since 1.0.0
 * 
 */
@Getter
public class DocumentFailedToDeleteException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainresponseDTO;

	/**
	 * Default constructore
	 */
	public DocumentFailedToDeleteException() {
		super();
	}

	/**
	 * @param errorMessage pass the error message
	 */
	public DocumentFailedToDeleteException(String errorMessage) {
		super(ErrorCodes.PRG_PAM_DOC_015.toString(), errorMessage);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public DocumentFailedToDeleteException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 */
	public DocumentFailedToDeleteException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainresponseDTO=response;
	}

	/**
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public DocumentFailedToDeleteException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_PAM_DOC_015.toString(), errorMessage, rootCause);
	}

	/**
	 * @param errorCode pass the error code
	 * @param errorMessage pass the error message
	 * @param rootCause pass the cause
	 */
	public DocumentFailedToDeleteException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
