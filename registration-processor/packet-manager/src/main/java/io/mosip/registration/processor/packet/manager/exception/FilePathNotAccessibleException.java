/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;

/**
 * FilePathNotAccessibleException occurs when file path is not accessible.
 *
 * @author Sowmya Goudar
 */
public class FilePathNotAccessibleException extends BaseUncheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file path not accessible exception.
	 */
	public FilePathNotAccessibleException() {
		super();

	}

	/**
	 * Instantiates a new file path not accessible exception.
	 *
	 * @param errorMessage the error message
	 */
	public FilePathNotAccessibleException(String errorMessage) {
		super(PlatformErrorCodes.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE, errorMessage);
	}

	/**
	 * Instantiates a new file path not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FilePathNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE + EMPTY_SPACE, message, cause);

	}





}
