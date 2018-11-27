package io.mosip.registration.processor.packet.archiver.util.exception.constant;

/**
 * The Enum PacketNotFoundExceptionConstant.
 * 
 * @author M1039285
 */
public enum PacketNotFoundExceptionConstant {

	/** The packet not found error. */
	PACKET_NOT_FOUND_ERROR("RER-ARC-001", "Packet Not Found in Packet Store");

	/** The error code. */
	public final String errorCode;

	/** The error message. */
	public final String errorMessage;

	/**
	 * Instantiates a new packet not found exception constant.
	 *
	 * @param string1
	 *            the string 1
	 * @param string2
	 *            the string 2
	 */
	PacketNotFoundExceptionConstant(String string1, String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
