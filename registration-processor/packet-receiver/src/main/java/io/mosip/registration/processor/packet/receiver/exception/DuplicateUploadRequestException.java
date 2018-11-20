package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
/**
 * DuplicateUploadRequestException occurs When a packet is already present in
 * landing zone and again client tries to upload same packet.
 */
public class DuplicateUploadRequestException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new duplicate upload request exception.
	 */
	public DuplicateUploadRequestException() {
		super();
	}

	/**
	 * Instantiates a new duplicate upload request exception.
	 *
	 * @param msg the msg
	 */
	public DuplicateUploadRequestException(String msg) {
		super(PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getCode(), msg);
	}

	/**
	 * Instantiates a new duplicate upload request exception.
	 *
	 * @param msg the msg
	 * @param cause the cause
	 */
	public DuplicateUploadRequestException(String msg, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getCode() + EMPTY_SPACE, msg, cause);
	}
}
