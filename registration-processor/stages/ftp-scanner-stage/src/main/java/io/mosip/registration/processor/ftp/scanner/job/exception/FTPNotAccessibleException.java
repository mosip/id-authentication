package io.mosip.registration.processor.ftp.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.ftp.scanner.job.exception.utils.FTPScannerErrorCodes;

public class FTPNotAccessibleException extends BaseUncheckedException {
	
	private static final long serialVersionUID = 1L;

	public FTPNotAccessibleException(String errorMessage) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	public FTPNotAccessibleException(String message, Throwable cause) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}
}