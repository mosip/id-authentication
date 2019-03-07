package io.mosip.kernel.idgenerator.prid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class PridException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1585042846828488115L;

	public PridException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
