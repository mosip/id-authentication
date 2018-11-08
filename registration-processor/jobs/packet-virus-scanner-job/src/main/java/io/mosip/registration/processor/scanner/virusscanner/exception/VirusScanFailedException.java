package io.mosip.registration.processor.scanner.virusscanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.scanner.virusscanner.exception.utils.VirusScannerErrorCodes;

public class VirusScanFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public VirusScanFailedException(String errorMessage) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED, errorMessage);
	}

	public VirusScanFailedException(String message, Throwable cause) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED + EMPTY_SPACE, message, cause);
	}

}
