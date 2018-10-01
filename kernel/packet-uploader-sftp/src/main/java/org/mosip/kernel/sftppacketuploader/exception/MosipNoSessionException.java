package org.mosip.kernel.sftppacketuploader.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.sftppacketuploader.constant.PacketUploaderConstants;
import org.mosip.kernel.sftppacketuploader.constant.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when there is no session
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNoSessionException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 4820565239606121727L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipNoSessionException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstants.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
