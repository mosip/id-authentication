package io.mosip.kernel.packetserver.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetserver.sftp.constant.PacketServerExceptionConstants;

/**
 * Exception to be thrown when a server goes to illegal state
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalStateException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 1384978616401586334L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipIllegalStateException(
			PacketServerExceptionConstants exceptionConstants,
			Throwable cause) {
		super(exceptionConstants.getErrorCode(),
				exceptionConstants.getErrorMessage(), cause);
	}

}
