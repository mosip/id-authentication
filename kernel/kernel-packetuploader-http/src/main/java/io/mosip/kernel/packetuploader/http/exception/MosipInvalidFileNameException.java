package io.mosip.kernel.packetuploader.http.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstant;
/**
 * Exception to be thrown when a file name is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidFileNameException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 7659309601631929476L;

	/**
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipInvalidFileNameException(PacketUploaderExceptionConstant exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
