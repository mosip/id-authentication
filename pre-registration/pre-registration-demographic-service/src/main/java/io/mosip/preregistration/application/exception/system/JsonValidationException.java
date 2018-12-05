package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class JsonValidationException extends BaseUncheckedException  {

	private static final long serialVersionUID = 1L;
	public JsonValidationException(String msg) {
		super("", msg);
	}
	public JsonValidationException(String errCode, String msg) {
		super(errCode, msg);
	}

	public JsonValidationException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
	
}
