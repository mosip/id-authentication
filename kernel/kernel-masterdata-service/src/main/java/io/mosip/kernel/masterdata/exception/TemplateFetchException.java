package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TemplateFetchException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -559306424003195247L;

	public TemplateFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
