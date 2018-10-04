/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * 
 * FileNotFoundInDestinationException occurs when file is not present 
 * in destination location 
 * 
 * @author Sowmya Goudar
 *
 */
public class FileNotFoundInDestinationException extends BaseUncheckedException{

	
	private static final long serialVersionUID = 1L;

	public FileNotFoundInDestinationException() {
		super();
		
	}

	public FileNotFoundInDestinationException(String errorMessage) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_DESTINATION, errorMessage);
	}

	public FileNotFoundInDestinationException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_DESTINATION + EMPTY_SPACE, message, cause);
		
	}
}
