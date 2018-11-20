package io.mosip.registration.processor.scanner.virusscanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class VirusScanFailedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public VirusScanFailedException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PSJ_VIRUS_SCAN_FAILED.getCode(), errorMessage);
	}

	public VirusScanFailedException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PSJ_VIRUS_SCAN_FAILED.getCode() + EMPTY_SPACE, message, cause);
	}

}
