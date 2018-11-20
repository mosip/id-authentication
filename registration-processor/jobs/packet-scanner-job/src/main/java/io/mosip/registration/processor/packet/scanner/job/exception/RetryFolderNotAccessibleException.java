package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
/**
 * The Class RetryFolderNotAccessibleException.
 */
public class RetryFolderNotAccessibleException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new retry folder not accessible exception.
	 *
	 * @param errorMessage the error message
	 */
	public RetryFolderNotAccessibleException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new retry folder not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RetryFolderNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE.getCode()+ EMPTY_SPACE, message, cause);
	}

}
