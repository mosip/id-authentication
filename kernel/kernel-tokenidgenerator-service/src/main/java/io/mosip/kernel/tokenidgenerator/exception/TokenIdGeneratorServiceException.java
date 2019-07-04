package io.mosip.kernel.tokenidgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TokenIdGeneratorServiceException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7918298357691506740L;

	public TokenIdGeneratorServiceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
