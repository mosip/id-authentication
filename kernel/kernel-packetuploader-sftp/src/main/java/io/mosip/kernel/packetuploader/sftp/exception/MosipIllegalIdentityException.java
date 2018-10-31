package io.mosip.kernel.packetuploader.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderConstant;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;

/**
 * Exception to be thrown when Private key is not valid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalIdentityException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = -7665593898258210837L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipIllegalIdentityException(PacketUploaderExceptionConstant exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstant.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
