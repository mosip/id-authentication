/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;


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
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE, errorMessage);
	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotFoundInSourceException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE + EMPTY_SPACE, message, cause);
		
	}

}
