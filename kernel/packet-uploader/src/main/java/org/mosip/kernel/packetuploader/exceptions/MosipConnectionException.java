package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseCheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

public class MosipConnectionException extends BaseCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	public MosipConnectionException() {

	}

	public MosipConnectionException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

	public MosipConnectionException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
