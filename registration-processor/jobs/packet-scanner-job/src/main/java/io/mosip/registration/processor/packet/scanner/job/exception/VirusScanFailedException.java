package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;
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
		super(RPRPlatformErrorCodes.RPR_PSJ_VIRUS_SCAN_FAILED, errorMessage);
	}

	/**
	 * Instantiates a new virus scan failed exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public VirusScanFailedException(String message, Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_PSJ_VIRUS_SCAN_FAILED + EMPTY_SPACE, message, cause);
	}

}
