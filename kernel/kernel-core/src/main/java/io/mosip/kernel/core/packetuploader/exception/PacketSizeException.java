package io.mosip.kernel.core.packetuploader.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when Packet is not right
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class PacketSizeException extends BaseUncheckedException {


	private static final long serialVersionUID = 3585613514626311385L;


	public PacketSizeException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}
}
