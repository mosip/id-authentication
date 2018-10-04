package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

public class PacketNotFoundException extends BaseCheckedException {

	private static final long serialVersionUID = 1L;

	public PacketNotFoundException() {
		super();

	}

	public PacketNotFoundException(String code, String message) {
		super(code, message);

	}

	public PacketNotFoundException(String code, String message, Throwable cause) {
		super(code, message, cause);

	}
}
