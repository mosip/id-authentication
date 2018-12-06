package io.mosip.kernel.idgenerator.tsp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TspIdServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9138117160521928565L;

	public TspIdServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
