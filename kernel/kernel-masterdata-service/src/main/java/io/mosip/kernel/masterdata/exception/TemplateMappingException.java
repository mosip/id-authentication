package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TemplateMappingException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -6907974779887410536L;


	public TemplateMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
