package io.mosip.kernel.sftppacketuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.kernel.sftppacketuploader.constant.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Path is Null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullPathException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = -2576609623548108679L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipNullPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
