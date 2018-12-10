package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class JsonParseException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public JsonParseException(String msg) {
		super("", msg);
	}

	public JsonParseException(String errCode, String msg) {
		super(errCode, msg);
	}

	public JsonParseException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
