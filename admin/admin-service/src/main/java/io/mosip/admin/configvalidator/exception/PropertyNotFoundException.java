package io.mosip.admin.configvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class PropertyNotFoundException extends BaseUncheckedException {

	public PropertyNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		// TODO Auto-generated constructor stub
	}

	public PropertyNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO Auto-generated constructor stub
	}

}
