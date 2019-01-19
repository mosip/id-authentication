package io.mosip.registration.processor.packet.storage.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class FileNotFoundInPacketStore.
 */
public class FileNotFoundInPacketStore extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new file not found in packet store.
	 */
	public FileNotFoundInPacketStore() {
		super();
	}
	
	/**
	 * Instantiates a new file not found in packet store.
	 *
	 * @param errorMessage the error message
	 */
	public FileNotFoundInPacketStore(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode()+ EMPTY_SPACE, errorMessage);
	}

	/**
	 * Instantiates a new file not found in packet store.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotFoundInPacketStore(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode() + EMPTY_SPACE, message, cause);
	}
	

}
