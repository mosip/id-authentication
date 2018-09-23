package org.mosip.kernel.httppacketuploader.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.httppacketuploader.constants.PacketUploaderExceptionConstants;

public class MosipInvalidFileName extends BaseUncheckedException {

	/**
	 * unique id for serialization
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
