package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


/**
 * Exception to be thrown when there is violation with SFTP protocol
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SFTPException extends BaseUncheckedException {

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
	public SFTPException(PacketUploaderExceptionConstant exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage()
				+ PacketUploaderConstant.EXCEPTTION_BREAKER.getValue() + cause.getMessage());
	}

}
