package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipSFTPException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4917690212566752247L;

	/**
	 * @param exceptionConstants
	 * @param cause
	 */
	public MosipSFTPException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage() + cause.getMessage());
	}

}
