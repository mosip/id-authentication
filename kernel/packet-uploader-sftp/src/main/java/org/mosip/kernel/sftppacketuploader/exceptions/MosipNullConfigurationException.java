package org.mosip.kernel.sftppacketuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when configuration is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullConfigurationException extends BaseUncheckedException {

	/**
	 * constant id for serialization
	 */
	private static final long serialVersionUID = -2256564750997889337L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipNullConfigurationException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
