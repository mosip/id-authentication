package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

public class MosipInvalidSessionException extends BaseUncheckedException{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4917690212566752247L;

	public MosipInvalidSessionException() {

	}

	public MosipInvalidSessionException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

	public MosipInvalidSessionException(PacketUploaderExceptionConstants exceptionConstants,String message) {
		super(exceptionConstants.getErrorCode(), message);
	}

}
