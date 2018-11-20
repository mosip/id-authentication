package io.mosip.registration.processor.scanner.ftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;



public class FTPNotAccessibleException extends BaseUncheckedException {
	
	private static final long serialVersionUID = 1L;

	public FTPNotAccessibleException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE.getCode(), errorMessage);
	}

	public FTPNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}


}
