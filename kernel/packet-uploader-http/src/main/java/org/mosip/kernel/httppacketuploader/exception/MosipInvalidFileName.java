package org.mosip.kernel.httppacketuploader.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.httppacketuploader.constant.PacketUploaderExceptionConstants;
/**
 * Exception to be thrown when a file name is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipInvalidFileName extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 7659309601631929476L;

	/**
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipInvalidFileName(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
