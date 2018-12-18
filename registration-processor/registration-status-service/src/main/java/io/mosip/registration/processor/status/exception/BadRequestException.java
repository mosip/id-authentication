package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class BadRequestException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super("400", message);
	}

	public BadRequestException(String message, Throwable cause) {
		super("400" + EMPTY_SPACE, message, cause);
	}
}

