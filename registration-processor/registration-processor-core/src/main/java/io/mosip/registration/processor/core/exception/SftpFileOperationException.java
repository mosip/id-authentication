/**
 * 
 */
package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;



public class SftpFileOperationException extends BaseCheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in source exception.
	 */
	public SftpFileOperationException() {
		super();

	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param errorMessage the error message
	 */
	public SftpFileOperationException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PKM_SFTP_FILE_OPERATION_FAILED.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public SftpFileOperationException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKM_SFTP_FILE_OPERATION_FAILED.getCode() + " ", message, cause);

	}

}
