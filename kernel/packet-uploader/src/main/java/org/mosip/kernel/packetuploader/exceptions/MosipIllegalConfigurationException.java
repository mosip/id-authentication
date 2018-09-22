package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalConfigurationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 925507057490535674L;

	/**
	 * @param exceptionConstants
	 * @param message
	 */
	public MosipIllegalConfigurationException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
