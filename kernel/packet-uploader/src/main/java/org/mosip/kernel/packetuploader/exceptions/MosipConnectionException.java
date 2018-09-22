package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseCheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipConnectionException extends BaseCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * @param exceptionConstants
	 * @param cause
	 */
	public MosipConnectionException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage() + cause.getMessage());
	}

}
