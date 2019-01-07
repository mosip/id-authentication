/**
 * 
 */
package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class DateParseException.
 *
 * @author M1047487
 */
public class DateParseException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new date parse exception.
	 */
	public DateParseException() {
		super();
	}
	
	/**
	 * Instantiates a new date parse exception.
	 *
	 * @param errorMessage the error message
	 */
	public DateParseException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new date parse exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public DateParseException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
