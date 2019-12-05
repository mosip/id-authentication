package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class IdRepoAppException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new identity not found exception.
	 */
	public IdRepoAppException() {
		super();
	}

	public IdRepoAppException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public IdRepoAppException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
	
	public IdRepoAppException(String errorCode, String errorMessage, String customMessage) {
		super(errorCode, errorMessage +"::" + customMessage);
	}

}
