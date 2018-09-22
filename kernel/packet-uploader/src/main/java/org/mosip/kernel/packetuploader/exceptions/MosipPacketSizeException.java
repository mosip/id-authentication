package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipPacketSizeException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * @param exceptionConstants
	 */
	public MosipPacketSizeException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}
}
