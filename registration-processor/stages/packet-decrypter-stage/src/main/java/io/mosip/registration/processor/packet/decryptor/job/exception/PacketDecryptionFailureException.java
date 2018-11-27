package io.mosip.registration.processor.packet.decryptor.job.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * PacketDecryptionFailureException class
 * @author Jyoti Prakash Nayak
 *
 */
public class PacketDecryptionFailureException extends BaseCheckedException{

	/**
	 * Serializable version Id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param code
	 *            Error Code Corresponds to Particular Exception
	 * @param message
	 *            Message providing the specific context of the error.
	 * @param cause
	 * 			  Throwable cause for the specific exception
	 */
	public PacketDecryptionFailureException(String code, String  message, Throwable cause) {
		super(code, message, cause);

	}

}
