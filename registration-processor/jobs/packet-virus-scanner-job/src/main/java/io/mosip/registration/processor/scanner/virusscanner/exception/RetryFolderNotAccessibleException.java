package io.mosip.registration.processor.scanner.virusscanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.scanner.virusscanner.exception.utils.VirusScannerErrorCodes;



public class RetryFolderNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RetryFolderNotAccessibleException(String errorMessage) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	public RetryFolderNotAccessibleException(String message, Throwable cause) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}

}
