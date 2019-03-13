package io.mosip.registration.processor.packet.service.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class FileNotAccessibleException extends BaseUncheckedException{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** Instantiate  a new filenotfoundexception. */
	public FileNotAccessibleException()
	{
	   super();
	}
	/**
	 * Instantiates a new file not accessible  exception.
	 *
	 * @param message the message
	 */
	public FileNotAccessibleException(String message) {
		super(PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getCode(), message);
	}

	/**
	 * Instantiates a new file not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FileNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getCode() + EMPTY_SPACE, message, cause);
	}

}
