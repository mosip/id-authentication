package io.mosip.registration.processor.packet.uploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class VirusScanFailedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new virus scan failed exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public VirusScanFailedException(String message) {
		super(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getCode(), message);
	}

	/**
	 * Instantiates a new virus scan failed exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public VirusScanFailedException(String message, Throwable cause) {
		super(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getCode(), message);
	}

}
