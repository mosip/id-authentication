/**
 * 
 */
package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;



public class JschConnectionException extends BaseCheckedException{


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not found in source exception.
	 */
	public JschConnectionException() {
		super();

	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param errorMessage the error message
	 */
	public JschConnectionException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PKM_JSCH_NOT_CONNECTED.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new file not found in source exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public JschConnectionException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKM_JSCH_NOT_CONNECTED.getCode() + " ", message, cause);

	}

}
