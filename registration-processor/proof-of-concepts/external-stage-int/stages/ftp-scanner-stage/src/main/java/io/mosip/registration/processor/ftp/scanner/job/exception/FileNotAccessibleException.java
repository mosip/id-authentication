package io.mosip.registration.processor.ftp.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.ftp.scanner.job.exception.utils.FTPScannerErrorCodes;
	
/**
 * The Class FileNotAccessibleException.
 */
public class FileNotAccessibleException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not accessible exception.
	 *
	 * @param errorMessage the error message
	 */
	public FileNotAccessibleException(String errorMessage) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_ACCESSIBLE, errorMessage);
	}

	/**
	 * Instantiates a new file not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotAccessibleException(String message, Throwable cause) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}
}