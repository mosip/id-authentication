/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * FileNotFoundInDestinationException occurs when file is not present 
 * in destination location .
 *
 * @author Sowmya Goudar
 */
public class FileNotFoundInDestinationException extends BaseUncheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in destination exception.
	 */
	public FileNotFoundInDestinationException() {
		super();

	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param errorMessage the error message
	 */
	public FileNotFoundInDestinationException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotFoundInDestinationException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getCode() + EMPTY_SPACE, message, cause);

	}
}
