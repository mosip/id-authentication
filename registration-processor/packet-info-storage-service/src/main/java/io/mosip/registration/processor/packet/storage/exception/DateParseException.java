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
public class DateParseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DateParseException() {
		super();
	}
	
	public DateParseException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public DateParseException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
