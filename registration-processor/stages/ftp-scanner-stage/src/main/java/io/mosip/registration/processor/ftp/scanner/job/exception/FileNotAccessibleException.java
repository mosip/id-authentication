package io.mosip.registration.processor.ftp.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.ftp.scanner.job.exception.utils.FTPScannerErrorCodes;

public class FileNotAccessibleException extends BaseUncheckedException {
	
	private static final long serialVersionUID = 1L;

	public FileNotAccessibleException(String errorMessage) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_ACCESSIBLE, errorMessage);
	}

	public FileNotAccessibleException(String message, Throwable cause) {
		super(FTPScannerErrorCodes.IIS_EPP_EPV_FILE_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}
}