package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

public class TokenGenerationFailedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new ABIS abort exception.
	 */
	public TokenGenerationFailedException() {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(), ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage());
	}

	/**
	 * Instantiates a new ABIS abort exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public TokenGenerationFailedException(String errorMessage) {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new ABIS abort exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public TokenGenerationFailedException(String message, Throwable cause) {
		super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode() + EMPTY_SPACE, message, cause);
	}
}

