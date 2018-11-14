package io.mosip.kernel.packetserver.sftp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.packetserver.sftp.constant.PacketServerExceptionConstant;

/**
 * Exception to be thrown when a key has invalid specs
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
 * 
 * @param errorCode
 * @param errorMessage
 */
	public InvalidSpecException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
