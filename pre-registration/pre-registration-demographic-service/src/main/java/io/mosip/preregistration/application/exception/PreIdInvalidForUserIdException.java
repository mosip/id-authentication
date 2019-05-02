package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;

/**
 * @author Jagadishwari
 * @since 1.0.0
 *
 */
public class PreIdInvalidForUserIdException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructore
	 */
	public PreIdInvalidForUserIdException() {
		super();
	}

	/**
	 * @param errorMessage
	 *            pass the error message
	 */
	public PreIdInvalidForUserIdException(String errorMessage) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), errorMessage);
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
	 * @param errorMessage
	 *            pass the error message
	 * @param rootCause
	 *            pass the cause
	 */
	public PreIdInvalidForUserIdException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_PAM_DOC_016.toString(), errorMessage, rootCause);
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
