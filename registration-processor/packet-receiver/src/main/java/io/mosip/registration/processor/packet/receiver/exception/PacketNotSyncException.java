package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

public class PacketNotSyncException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public PacketNotSyncException() {
		super();
	}

	public PacketNotSyncException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_SYNC, message);
	}

	public PacketNotSyncException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_SYNC + EMPTY_SPACE, message, cause);
	}
}