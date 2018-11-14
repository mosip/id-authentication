package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TitleFetchException extends BaseUncheckedException {

	private static final long serialVersionUID = -1154779999212799100L;

	public TitleFetchException() {
		super();

	}

	public TitleFetchException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public TitleFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public TitleFetchException(String errorMessage) {
		super(errorMessage);

	}

}
