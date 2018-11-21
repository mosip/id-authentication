package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class JsonValidationException extends BaseUncheckedException  {

	private static final long serialVersionUID = 1L;

	public JsonValidationException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public JsonValidationException(String msg) {
		super("", msg);
	}
}
