package io.mosip.kernel.filesystem.adapter.impl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.filesystem.adapter.impl.utils.PlatformErrorMessages;

public class CephRunTimeException  extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new connection unavailable exception.
	 */
	public CephRunTimeException() {
		super();
	}

	/**
	 * Instantiates a new connection unavailable exception.
	 *
	 * @param message
	 *            the message
	 */
	public CephRunTimeException(String message) {
		super(PlatformErrorMessages.KER_FAC_CONNECTION_NOT_AVAILABLE.getCode(), message);
	}

	/**
	 * Instantiates a new connection unavailable exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CephRunTimeException(String message, Throwable cause) {
		super(PlatformErrorMessages.KER_FAC_CONNECTION_NOT_AVAILABLE.getCode() + EMPTY_SPACE, message, cause);
	}


}
