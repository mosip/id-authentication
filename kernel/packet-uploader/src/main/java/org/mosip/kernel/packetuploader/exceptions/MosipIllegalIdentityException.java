package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalIdentityException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7665593898258210837L;

	/**
	 * @param exceptionConstants
	 * @param cause
	 */
	public MosipIllegalIdentityException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage() + cause.getMessage());
	}

}
