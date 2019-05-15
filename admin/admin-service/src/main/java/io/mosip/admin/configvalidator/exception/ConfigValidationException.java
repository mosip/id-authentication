package io.mosip.admin.configvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ConfigValidationException extends BaseUncheckedException {

	public ConfigValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		// TODO Auto-generated constructor stub
	}

	public ConfigValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO Auto-generated constructor stub
	}

}
