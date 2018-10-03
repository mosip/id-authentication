package io.mosip.kernel.packetserver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.kernel.packetserver.constant.PacketServerExceptionConstants;

/**
 * Exception to be thrown when public key is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipPublicKeyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -8867318998929810105L;

	/**
	 * Constructor for this class
	 * 
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipPublicKeyException(
			PacketServerExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(),
				exceptionConstants.getErrorMessage());
	}

}
