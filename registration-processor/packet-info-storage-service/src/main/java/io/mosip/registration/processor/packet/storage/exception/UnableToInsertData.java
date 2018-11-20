package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.storage.exception.code.PacketMetaInfoErrorCode;

public class UnableToInsertData extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnableToInsertData() {
		super();
	}
	
	public UnableToInsertData(String errorMessage) {
		super(PacketMetaInfoErrorCode.UNABLE_TO_INSERT_DATA + EMPTY_SPACE, errorMessage);
	}

	public UnableToInsertData(String message, Throwable cause) {
		super(PacketMetaInfoErrorCode.UNABLE_TO_INSERT_DATA + EMPTY_SPACE, message, cause);
	}

}
