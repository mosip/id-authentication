package io.mosip.registration.processor.virus.scanner.job.exceptions;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.virus.scanner.job.exceptions.util.VirusScannerJobErrorCodes;

/**
 * The Class VirusScanFailedException.
 */
public class VirusScanFailedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new virus scan failed exception.
	 *
	 * @param errorMessage the error message
	 */
	public VirusScanFailedException(String errorMessage) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED, errorMessage);
	}

	/**
	 * Instantiates a new virus scan failed exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public VirusScanFailedException(String message, Throwable cause) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_VIRUS_SCAN_FAILED + EMPTY_SPACE, message, cause);
	}

}
