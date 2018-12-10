package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class ParsingException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ParsingException() {
		super();
	}
	
	public ParsingException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public ParsingException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
