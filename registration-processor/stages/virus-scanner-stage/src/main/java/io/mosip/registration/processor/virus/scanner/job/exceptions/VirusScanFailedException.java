package io.mosip.registration.processor.virus.scanner.job.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.util.VirusScannerJobErrorCodes;


public class VirusScanFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public VirusScanFailedException(String errorMessage) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED, errorMessage);
	}

	public VirusScanFailedException(String message, Throwable cause) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED + EMPTY_SPACE, message, cause);
	}

}
