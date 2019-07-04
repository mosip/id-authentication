/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;


/**
 * FileNotFoundInSourceException occurs when file is not present 
 * in source location .
 *
 * @author Sowmya Goudar
 */
public class FileNotFoundInSourceException extends BaseUncheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in source exception.
	 */
	public FileNotFoundInSourceException() {
		super();

	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param errorMessage the error message
	 */
	public FileNotFoundInSourceException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotFoundInSourceException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getCode() + EMPTY_SPACE, message, cause);

	}

}
