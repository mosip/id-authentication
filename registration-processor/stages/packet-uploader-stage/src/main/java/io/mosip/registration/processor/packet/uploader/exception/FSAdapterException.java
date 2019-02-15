package io.mosip.registration.processor.packet.uploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class DFSNotAccessibleException.
 */
public class FSAdapterException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new PACKET STORE not accessible exception.
	 */
	public FSAdapterException() {
		super();
	}

	/**
	 * Instantiates a new PACKET STORE not accessible exception.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	public FSAdapterException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PUM_PACKET_STORE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new PACKET STORE not accessible exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public FSAdapterException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PUM_PACKET_STORE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}

}
