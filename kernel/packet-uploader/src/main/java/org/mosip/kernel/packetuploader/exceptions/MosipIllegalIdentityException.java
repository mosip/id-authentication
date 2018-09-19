package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

public class MosipIllegalIdentityException extends BaseUncheckedException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7665593898258210837L;

	public MosipIllegalIdentityException() {

	}

	public MosipIllegalIdentityException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

	public MosipIllegalIdentityException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
