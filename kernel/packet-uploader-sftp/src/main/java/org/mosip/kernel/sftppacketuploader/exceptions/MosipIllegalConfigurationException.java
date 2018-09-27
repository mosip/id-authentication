package org.mosip.kernel.sftppacketuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.sftppacketuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Configuration are not valid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalConfigurationException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = 925507057490535674L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipIllegalConfigurationException(PacketUploaderExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
