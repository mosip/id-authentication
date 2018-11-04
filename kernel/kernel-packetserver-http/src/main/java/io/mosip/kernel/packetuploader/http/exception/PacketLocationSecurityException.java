package io.mosip.kernel.packetuploader.http.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetuploader.http.constant.PacketUploaderExceptionConstant;
/**
 * Exception to be thrown when a directory exist which is not Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketLocationSecurityException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 737066617944035252L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 * @param cause
	 *            cause of exception
	 */
	public PacketLocationSecurityException(PacketUploaderExceptionConstant exceptionConstants, Throwable cause) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage(), cause);
	}

}
