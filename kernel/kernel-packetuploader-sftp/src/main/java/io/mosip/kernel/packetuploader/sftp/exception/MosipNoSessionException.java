package io.mosip.kernel.packetuploader.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;

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
	public MosipNoSessionException(PacketUploaderExceptionConstant exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstant.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
