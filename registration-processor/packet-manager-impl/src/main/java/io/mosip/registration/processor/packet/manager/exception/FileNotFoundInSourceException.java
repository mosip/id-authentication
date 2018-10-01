/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;


/**
 * 
 * FileNotFoundInSourceException occurs when file is not present 
 * in source location 
 * 
 * @author Sowmya Goudar
 *
 */
public class FileNotFoundInSourceException extends BaseUncheckedException{

	
	private static final long serialVersionUID = 1L;

	public FileNotFoundInSourceException() {
		super();
		
	}

	public FileNotFoundInSourceException(String errorMessage) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE, errorMessage);
	}

	public FileNotFoundInSourceException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE + EMPTY_SPACE, message, cause);
		
	}

}
