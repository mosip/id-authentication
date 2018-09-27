package org.mosip.kernel.packetserver.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetserver.constants.PacketServerExceptionConstants;

/**
 * Exception to be thrown when a server goes to illegal state
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalStateException extends BaseUncheckedException {

	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = 1384978616401586334L;

	/**
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipIllegalStateException(PacketServerExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

}
