package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class MappingJsonException.
 */
public class MappingJsonException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new mapping json exception.
	 */
	public MappingJsonException() {
		super();
	}
	
	/**
	 * Instantiates a new mapping json exception.
	 *
	 * @param errorMessage the error message
	 */
	public MappingJsonException(String errorMessage) {
		super(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getCode()+ EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new mapping json exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public MappingJsonException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getCode() + EMPTY_SPACE, message, cause);
	}

}
