package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Packet is not right
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipPacketSizeException extends BaseUncheckedException {

	/**
	 * constant id for serialization
	 */
	private static final long serialVersionUID = 3585613514626311385L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipPacketSizeException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}
}
