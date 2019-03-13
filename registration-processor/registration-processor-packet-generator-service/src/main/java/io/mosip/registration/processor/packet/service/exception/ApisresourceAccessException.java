package io.mosip.registration.processor.packet.service.exception;
import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ApisresourceAccessException extends BaseUncheckedException{
	
	/** instantiate a new ApisresourceAccessException. */
	public ApisresourceAccessException()
	{
		super();
	}
	
    /**
     * instantiate a new ApisresourceAccessException.
     * 
     * @Param message the message.
     */
	public ApisresourceAccessException(String message)
	{
		super(message);
	}
}
