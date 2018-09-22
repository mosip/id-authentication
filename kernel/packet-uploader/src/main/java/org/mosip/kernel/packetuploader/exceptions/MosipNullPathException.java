package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullPathException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2576609623548108679L;

	/**
	 * @param exceptionConstants
	 * @param message
	 */
	public MosipNullPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
