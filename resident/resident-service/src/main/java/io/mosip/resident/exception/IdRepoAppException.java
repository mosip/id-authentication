package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

public class IdRepoAppException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new identity not found exception.
	 */
	public IdRepoAppException() {
		super();
	}

	/**
	 * Instantiates a new identity not found exception.
	 *
	 * @param errorMessage' the error message
	 */
	public IdRepoAppException(String errorMessage) {
		super(ResidentErrorCode.IN_VALID_UIN_OR_RID.getErrorCode(), errorMessage);
	}

	/**
	 * Instantiates a new identity not found exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public IdRepoAppException(String message, Throwable cause) {
		super(ResidentErrorCode.IN_VALID_UIN_OR_RID.getErrorCode(), message, cause);
	}

}
