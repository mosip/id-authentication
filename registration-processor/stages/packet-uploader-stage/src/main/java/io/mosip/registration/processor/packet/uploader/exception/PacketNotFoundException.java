package io.mosip.registration.processor.packet.uploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class PacketNotFoundException.
 * 
 * @author M1039285
 */
public class PacketNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PacketNotFoundException() {
		super();
	}

	public PacketNotFoundException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode() + EMPTY_SPACE, errorMessage);
	}

	public PacketNotFoundException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getCode() + EMPTY_SPACE, message, cause);
	}
}
