package org.mosip.kernel.sftppacketuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderConstants;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when there is violation with SFTP protocol
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipSFTPException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = -4917690212566752247L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipSFTPException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstants.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
