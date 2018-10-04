package io.mosip.registration.processor.packet.archiver.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

public class UnableToAccessPathException extends BaseCheckedException {

	private static final long serialVersionUID = 1L;

	public UnableToAccessPathException(String code, String message, Throwable cause) {
		super(code, message, cause);

	}
}
