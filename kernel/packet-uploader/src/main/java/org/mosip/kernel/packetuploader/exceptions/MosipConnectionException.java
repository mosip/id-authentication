package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseCheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderConstants;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Connection is not made with server
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipConnectionException extends BaseCheckedException {

	/**
	 * constant id for serialization
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipConnectionException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstants.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
