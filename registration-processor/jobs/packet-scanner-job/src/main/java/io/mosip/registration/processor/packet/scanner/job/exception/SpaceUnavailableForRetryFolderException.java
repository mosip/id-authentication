package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
/**
 * The Class SpaceUnavailableForRetryFolderException.
 */
public class SpaceUnavailableForRetryFolderException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new space unavailable for retry folder exception.
	 *
	 * @param errorMessage the error message
	 */
	public SpaceUnavailableForRetryFolderException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new space unavailable for retry folder exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public SpaceUnavailableForRetryFolderException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER.getCode() + EMPTY_SPACE, message, cause);
	}

}
