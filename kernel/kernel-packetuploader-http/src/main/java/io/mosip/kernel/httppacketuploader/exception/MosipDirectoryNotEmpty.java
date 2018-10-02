package io.mosip.kernel.httppacketuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.kernel.httppacketuploader.constant.PacketUploaderExceptionConstants;

/**
 * Exception to be thrown when a directory exist which is not Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipDirectoryNotEmpty extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -381238520404127950L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public MosipDirectoryNotEmpty(PacketUploaderExceptionConstants exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

}
