package io.mosip.registration.processor.message.sender.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class IDRepoResponseNull extends BaseUncheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in destination exception.
	 */
	public IDRepoResponseNull() {
		super();

	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param errorMessage the error message
	 */
	public IDRepoResponseNull(String errorMessage) {
		super(PlatformErrorMessages.RPR_PRT_IDREPO_RESPONSE_NULL.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in destination exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public IDRepoResponseNull(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PRT_IDREPO_RESPONSE_NULL.getCode() + EMPTY_SPACE, message, cause);

	}
}

