package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RecordFailedToDeleteException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RecordFailedToDeleteException() {
		super();
	}

	public RecordFailedToDeleteException(String errorMessage) {
		super("", errorMessage);
	}

	public RecordFailedToDeleteException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public RecordFailedToDeleteException(String errorMessage, Throwable rootCause) {
		super("", errorMessage, rootCause);
	}

	public RecordFailedToDeleteException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
