package io.mosip.registration.processor.packet.uploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

// TODO: Auto-generated Javadoc
/**
 * The Class UnequalHashSequenceException.
 */
public class UnequalHashSequenceException  extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new unequal hash sequence exception.
	 */
	public UnequalHashSequenceException()
	{
		super();
	}
	
	/**
	 * Instantiates a new unequal hash sequence exception.
	 *
	 * @param message the message
	 */
	public UnequalHashSequenceException(String message)
	{
		super(PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getCode(),message);
	}
	
	/**
	 * Instantiates a new unequal hash sequence exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public UnequalHashSequenceException(String message,Throwable cause)
	{
		super(PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getCode()+EMPTY_SPACE,message,cause);
	}
}
