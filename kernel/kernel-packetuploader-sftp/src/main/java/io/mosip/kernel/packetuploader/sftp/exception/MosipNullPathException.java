package io.mosip.kernel.packetuploader.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstant;

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
	public MosipNullPathException(PacketUploaderExceptionConstant exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
