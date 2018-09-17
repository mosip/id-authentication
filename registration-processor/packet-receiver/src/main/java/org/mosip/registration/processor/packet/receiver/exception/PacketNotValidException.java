package org.mosip.registration.processor.packet.receiver.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * PacketNotValidException occurs when the file received
 * is not as per the specified format
 *
 */
public class PacketNotValidException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public PacketNotValidException() {
		super();
	}

	public PacketNotValidException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_PACKET, message);
	}

	public PacketNotValidException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_INVALID_PACKET, message, cause);
	}
}
