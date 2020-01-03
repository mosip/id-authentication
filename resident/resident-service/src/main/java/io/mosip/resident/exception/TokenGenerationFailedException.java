package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

public class TokenGenerationFailedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;


	public TokenGenerationFailedException() {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(), ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage());
	}

	public TokenGenerationFailedException(String errorMessage) {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode() + EMPTY_SPACE, errorMessage);
	}

	public TokenGenerationFailedException(String message, Throwable cause) {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode() + EMPTY_SPACE, message, cause);
	}
}

