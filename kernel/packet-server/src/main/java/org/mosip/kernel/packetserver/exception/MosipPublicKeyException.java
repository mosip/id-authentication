package org.mosip.kernel.packetserver.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.kernel.packetserver.constants.PacketServerExceptionConstants;

/**
 * Exception to be thrown when public key is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipPublicKeyException extends BaseUncheckedException {

	/**
	 * unique id for serialization
	 */
	private static final long serialVersionUID = -8867318998929810105L;

	/**
	 * @param exceptionConstants
	 *            exception code constant
	 */
	public MosipPublicKeyException(PacketServerExceptionConstants exceptionConstants) {
		super(exceptionConstants.getErrorCode(), exceptionConstants.getErrorMessage());
	}

}
