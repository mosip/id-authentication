package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.errorcodes.AbstractVerticleErrorCodes;

/**
 * The Class UnsupportedEncodingException.
 */
public class UnsupportedEncodingException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new unsupported encoding exception.
	 */
	public UnsupportedEncodingException() {
		super();
	}

	/**
	 * Instantiates a new unsupported encoding exception.
	 *
	 * @param message
	 *            the message
	 */
	public UnsupportedEncodingException(String message) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE, message);
	}

	/**
	 * Instantiates a new unsupported encoding exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public UnsupportedEncodingException(String message, Throwable cause) {
		super(AbstractVerticleErrorCodes.IIS_EPU_ATU_DEPLOYMENT_FAILURE + EMPTY_SPACE, message, cause);
	}

}
