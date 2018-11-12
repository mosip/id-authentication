package io.mosip.kernel.packetserver.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when public key is invalid
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PublicKeyException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -8867318998929810105L;

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public PublicKeyException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
