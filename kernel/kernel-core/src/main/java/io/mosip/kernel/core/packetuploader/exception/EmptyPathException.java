package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when Path is Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class EmptyPathException extends BaseUncheckedException {

	/**
	 * Constant id for serialization
	 */
	private static final long serialVersionUID = -4601559589099809931L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public EmptyPathException(PacketUploaderExceptionConstant exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
