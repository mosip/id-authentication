package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when configuration is null
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class NullConfigurationException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = -2256564750997889337L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public NullConfigurationException(PacketUploaderExceptionConstant exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
