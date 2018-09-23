package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

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
	 * @param cause
	 *            cause of exception
	 */
	public MosipEmptyPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
