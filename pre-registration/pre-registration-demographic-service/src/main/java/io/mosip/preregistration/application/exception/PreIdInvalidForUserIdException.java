package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Jagadishwari
 * @since 1.0.0
 *
 */
@Getter
public class PreIdInvalidForUserIdException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructore
	 */
	public PreIdInvalidForUserIdException() {
		super();
	}

	private MainResponseDTO<?> mainresponseDTO;

	/**
	 * @param errorMessage
	 *            pass the error message
	 */
	public PreIdInvalidForUserIdException(String errorMessage) {
		super("", errorMessage);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 */
	public PreIdInvalidForUserIdException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 */
	public PreIdInvalidForUserIdException(String errorCode, String errorMessage, MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainresponseDTO = response;
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public PreIdInvalidForUserIdException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public PreIdInvalidForUserIdException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
