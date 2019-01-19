package io.mosip.kernel.packetserver.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when a key has invalid Specs
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class InvalidSpecException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 518734974933227166L;

	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public InvalidSpecException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
