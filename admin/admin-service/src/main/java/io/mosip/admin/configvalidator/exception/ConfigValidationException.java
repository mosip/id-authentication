package io.mosip.admin.configvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sidhant Agarwal
 * @sice 1.0.0
 *
 */
public class ConfigValidationException extends BaseUncheckedException {

	public ConfigValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		
	}

	public ConfigValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

}
