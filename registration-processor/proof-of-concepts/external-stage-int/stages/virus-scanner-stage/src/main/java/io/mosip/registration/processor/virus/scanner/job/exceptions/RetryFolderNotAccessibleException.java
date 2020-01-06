package io.mosip.registration.processor.virus.scanner.job.exceptions;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.util.VirusScannerJobErrorCodes;


/**
 * The Class RetryFolderNotAccessibleException.
 */
public class RetryFolderNotAccessibleException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new retry folder not accessible exception.
	 *
	 * @param errorMessage the error message
	 */
	public RetryFolderNotAccessibleException(String errorMessage) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	/**
	 * Instantiates a new retry folder not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RetryFolderNotAccessibleException(String message, Throwable cause) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}

}
