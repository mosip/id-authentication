/**
 * 
 */
package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class StreamToBytesConversionException.
 *
 * @author M1047487
 */
public class StreamToBytesConversionException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new stream to bytes conversion exception.
	 */
	public StreamToBytesConversionException() {
		super();
	}

	/**
	 * Instantiates a new stream to bytes conversion exception.
	 *
	 * @param errorMessage the error message
	 */
	public StreamToBytesConversionException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES.getCode() + EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new stream to bytes conversion exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public StreamToBytesConversionException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES.getCode() + EMPTY_SPACE, message, cause);
	}
}
