/**
 * 
 */
package org.mosip.registration.processor.packet.manager.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * 
 * FilePathNotAccessibleException occurs when file path is not accessible
 * 
 * 
 * @author Sowmya Goudar
 *
 */
public class FilePathNotAccessibleException extends BaseUncheckedException{

	
	private static final long serialVersionUID = 1L;

	public FilePathNotAccessibleException() {
		super();
		
	}

	public FilePathNotAccessibleException(String errorMessage) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_PATH_NOT_ACCESSIBLE, errorMessage);
	}

	public FilePathNotAccessibleException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_PATH_NOT_ACCESSIBLE + EMPTY_SPACE, message, cause);
		
	}
	
	
	
	

}
