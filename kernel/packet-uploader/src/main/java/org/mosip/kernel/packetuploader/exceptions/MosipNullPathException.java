package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Path is Null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipNullPathException extends BaseUncheckedException {

	/**
	 * constant id for serialization
	 */
	private static final long serialVersionUID = -2576609623548108679L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipNullPathException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
