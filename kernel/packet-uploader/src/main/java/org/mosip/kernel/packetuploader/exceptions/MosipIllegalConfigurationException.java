package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

public class MosipIllegalConfigurationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 925507057490535674L;

	public MosipIllegalConfigurationException() {

	}

	public MosipIllegalConfigurationException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

	public MosipIllegalConfigurationException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
