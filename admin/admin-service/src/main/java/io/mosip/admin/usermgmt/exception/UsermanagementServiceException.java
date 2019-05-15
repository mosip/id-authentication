package io.mosip.admin.usermgmt.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class UsermanagementServiceException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3383837827871687253L;

	public UsermanagementServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public UsermanagementServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}
}
