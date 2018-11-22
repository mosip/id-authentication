package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorConstants;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class MappingJsonException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MappingJsonException() {
		super();
	}
	
	public MappingJsonException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public MappingJsonException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
