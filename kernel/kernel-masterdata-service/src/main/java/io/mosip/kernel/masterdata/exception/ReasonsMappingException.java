package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ReasonsMappingException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4720465883391239064L;
	
	public ReasonsMappingException(final String errorCode,final String errorMessage) {
		super(errorCode,errorMessage);
	}

}
