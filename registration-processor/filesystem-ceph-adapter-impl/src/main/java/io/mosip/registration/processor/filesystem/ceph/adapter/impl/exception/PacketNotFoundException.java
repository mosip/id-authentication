package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception;


import org.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.IISPlatformErrorCodes;

/**
 * PacketNotFoundException occurs when requested packet is not present
 * in DFS
 */
public class PacketNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public PacketNotFoundException() {
		super();
	}

	public PacketNotFoundException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_AVAILABLE + EMPTY_SPACE, message);
	}

	public PacketNotFoundException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_PACKET_NOT_AVAILABLE, message, cause);
	}
}