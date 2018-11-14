package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ApplicationMappingException extends BaseUncheckedException {

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -7127912469221923106L;
	
	public ApplicationMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
