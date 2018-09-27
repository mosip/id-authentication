package org.mosip.kernel.httppacketuploader.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.httppacketuploader.constants.PacketUploaderExceptionConstants;

public class MosipPacketLocationSecurity extends BaseUncheckedException {

	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = 737066617944035252L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipPacketLocationSecurity(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

}
