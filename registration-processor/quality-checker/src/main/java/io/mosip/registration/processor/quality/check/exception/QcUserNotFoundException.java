package io.mosip.registration.processor.quality.check.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.quality.check.exception.utils.IISPlatformErrorCodes;

public class QcUserNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QcUserNotFoundException() {
		super();
	}

	public QcUserNotFoundException(String message) {
		super(IISPlatformErrorCodes.IIS_QCV_USER_ID_NOT_FOUND, message);
	}

	public QcUserNotFoundException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_QCV_USER_ID_NOT_FOUND + EMPTY_SPACE, message, cause);
	}
}