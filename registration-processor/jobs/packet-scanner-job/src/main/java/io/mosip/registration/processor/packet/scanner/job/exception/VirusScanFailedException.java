package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class VirusScanFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public VirusScanFailedException(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED, errorMessage);
	}

	public VirusScanFailedException(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED + EMPTY_SPACE, message, cause);
	}

}
