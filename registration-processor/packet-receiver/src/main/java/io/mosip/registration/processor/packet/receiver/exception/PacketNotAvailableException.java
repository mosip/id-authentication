package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * PacketNotAvailableException occurs when the file is
 * not present in the request
 *
 */
public class PacketNotAvailableException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public PacketNotAvailableException() {
		super();
	}

	public PacketNotAvailableException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_AVAILABLE, message);
	}

	public PacketNotAvailableException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_AVAILABLE + EMPTY_SPACE, message, cause);
	}
}
