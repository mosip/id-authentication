package io.mosip.registration.processor.packet.service.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sowmya The Class RegBaseUnCheckedException.
 */
public class RegBaseUnCheckedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3422834488678859452L;

	/**
	 * Constructs a new unchecked exception.
	 */
	public RegBaseUnCheckedException() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 */
	public RegBaseUnCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	/**
	 * Constructor.
	 *
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 * @param throwable
	 *            the Cause of exception
	 */
	public RegBaseUnCheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);

	}
}
