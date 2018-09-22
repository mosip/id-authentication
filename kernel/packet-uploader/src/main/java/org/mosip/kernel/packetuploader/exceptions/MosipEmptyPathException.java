package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipEmptyPathException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4601559589099809931L;

	/**
	 * @param exceptionConstants
	 * @param cause
	 */
	public MosipEmptyPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
