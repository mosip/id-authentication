package org.mosip.registration.processor.packet.scanner.job.exception;


import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class FTPNotAccessibleException extends BaseUncheckedException {
	
	private static final long serialVersionUID = 1L;

	public FTPNotAccessibleException(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	public FTPNotAccessibleException(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE+ EMPTY_SPACE, message, cause);
	}


}
