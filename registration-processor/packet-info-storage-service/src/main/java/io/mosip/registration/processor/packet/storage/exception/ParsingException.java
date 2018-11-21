package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorConstants;

public class ParsingException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ParsingException() {
		super();
	}
	
	public ParsingException(String errorMessage) {
		super(PlatformErrorConstants.MAPPING_JSON_EXCEPTION+ EMPTY_SPACE, errorMessage);
	}

	public ParsingException(String message, Throwable cause) {
		super(PlatformErrorConstants.MAPPING_JSON_EXCEPTION + EMPTY_SPACE, message, cause);
	}

}
