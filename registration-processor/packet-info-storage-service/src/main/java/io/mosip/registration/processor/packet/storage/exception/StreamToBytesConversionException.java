/**
 * 
 */
package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * @author M1047487
 *
 */
public class StreamToBytesConversionException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StreamToBytesConversionException() {
		super();
	}

	public StreamToBytesConversionException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES.getCode() + EMPTY_SPACE, errorMessage);
	}

	public StreamToBytesConversionException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES.getCode() + EMPTY_SPACE, message, cause);
	}
}
