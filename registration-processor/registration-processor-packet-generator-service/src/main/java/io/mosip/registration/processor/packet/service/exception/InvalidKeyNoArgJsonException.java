package io.mosip.registration.processor.packet.service.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class InvalidKeyNoArgJsonException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** Instantiate a new InvalidKetNoArgJsonException. */
	public InvalidKeyNoArgJsonException()
	{
		super();
	}
	
	/** instantiate a new InvalidKetNoArgJsonException
	 * 
	 * @Param message the message
	 */
	public InvalidKeyNoArgJsonException(String message)
	{
		super(PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getCode(), message);
	}
}
