package io.mosip.registration.processor.packet.uploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class VirusScannerServiceException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new virus scanner service exception.
	 */
	public VirusScannerServiceException() {
		super();
	}

	/**
	 * Instantiates a new virus scanner service exception.
	 *
	 * @param message
	 *            the message
	 */
	public VirusScannerServiceException(String message) {
		super(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getCode(), message);
	}

	/**
	 * Instantiates a new virus scanner service exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public VirusScannerServiceException(String message, Throwable cause) {
		super(PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getCode() + EMPTY_SPACE, message,
				cause);
	}
}
