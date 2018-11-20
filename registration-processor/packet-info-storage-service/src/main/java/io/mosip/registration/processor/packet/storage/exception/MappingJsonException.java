package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.storage.exception.code.PacketMetaInfoErrorCode;

public class MappingJsonException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MappingJsonException() {
		super();
	}
	
	public MappingJsonException(String errorMessage) {
		super(PacketMetaInfoErrorCode.MAPPING_JSON_EXCEPTION+ EMPTY_SPACE, errorMessage);
	}

	public MappingJsonException(String message, Throwable cause) {
		super(PacketMetaInfoErrorCode.MAPPING_JSON_EXCEPTION + EMPTY_SPACE, message, cause);
	}

}
