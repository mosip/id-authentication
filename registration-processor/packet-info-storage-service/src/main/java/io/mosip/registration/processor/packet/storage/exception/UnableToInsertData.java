package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class UnableToInsertData extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnableToInsertData() {
		super();
	}
	
	public UnableToInsertData(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getCode() + EMPTY_SPACE, errorMessage);
	}

	public UnableToInsertData(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getCode() + EMPTY_SPACE, message, cause);
	}

}
