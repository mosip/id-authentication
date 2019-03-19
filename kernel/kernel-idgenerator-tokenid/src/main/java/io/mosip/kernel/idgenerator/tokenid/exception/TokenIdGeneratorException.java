package io.mosip.kernel.idgenerator.tokenid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TokenIdGeneratorException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 923629062510387031L;

	public TokenIdGeneratorException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
