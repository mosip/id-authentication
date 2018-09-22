package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullConfigurationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2256564750997889337L;

	/**
	 * @param exceptionConstants
	 * @param message
	 */
	public MosipNullConfigurationException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
