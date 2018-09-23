package org.mosip.kernel.packetuploader.exceptions;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetuploader.constants.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when Configuration are not valid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIllegalConfigurationException extends BaseUncheckedException {

	/**
	 * constant id for serialization
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
