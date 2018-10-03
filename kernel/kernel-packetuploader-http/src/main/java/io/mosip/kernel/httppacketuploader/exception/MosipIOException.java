package io.mosip.kernel.httppacketuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.kernel.httppacketuploader.constant.PacketUploaderExceptionConstants;
/**
 * Exception to be thrown when a io exception occur 
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipIOException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -7201638126622777034L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipIOException(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

}
