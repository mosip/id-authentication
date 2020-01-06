package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
	 * @param errorMessage'
	 *            the error message
	 */
	public IdRepoAppException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PVM_INVALID_UIN.getCode() + EMPTY_SPACE, PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage()+ "-" +errorMessage);
	}

	/**
	 * Instantiates a new identity not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public IdRepoAppException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PVM_INVALID_UIN.getCode() + EMPTY_SPACE, PlatformErrorMessages.RPR_PVM_INVALID_UIN.getMessage()+"-" +message, cause);
	}

}
