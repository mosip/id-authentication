package org.mosip.kernel.sftppacketuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Path is Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipEmptyPathException extends BaseUncheckedException {

	/**
	 * constant id for serialization
	 */
	private static final long serialVersionUID = -4601559589099809931L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipEmptyPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
