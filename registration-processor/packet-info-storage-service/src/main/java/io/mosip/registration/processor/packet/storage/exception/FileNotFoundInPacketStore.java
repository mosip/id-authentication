package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class FileNotFoundInPacketStore extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FileNotFoundInPacketStore() {
		super();
	}
	
	public FileNotFoundInPacketStore(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode()+ EMPTY_SPACE, errorMessage);
	}

	public FileNotFoundInPacketStore(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode() + EMPTY_SPACE, message, cause);
	}
	

}
