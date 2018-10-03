package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class VirusScanFolderNotAccessibleEception extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public VirusScanFolderNotAccessibleEception(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE, errorMessage);
	}

	public VirusScanFolderNotAccessibleEception(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}
