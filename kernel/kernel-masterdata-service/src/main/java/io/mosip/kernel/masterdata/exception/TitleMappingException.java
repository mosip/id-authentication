package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TitleMappingException extends BaseUncheckedException {

	private static final long serialVersionUID = -1154778480214449100L;

	public TitleMappingException() {
		super();

	}

	public TitleMappingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public TitleMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public TitleMappingException(String errorMessage) {
		super(errorMessage);

	}

}
