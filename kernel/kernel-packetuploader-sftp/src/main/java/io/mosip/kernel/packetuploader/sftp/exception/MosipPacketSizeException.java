package io.mosip.kernel.packetuploader.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.sftp.constant.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Packet is not right
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipPacketSizeException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
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
