package io.mosip.registration.processor.packet.decryptor.job.exception.constant;
	
/**
 * The Enum PacketDecryptionFailureExceptionConstant.
 */
public enum PacketDecryptionFailureExceptionConstant {
	
	/** The mosip packet decryption failure error code. */
	MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE("RPR-PKD-004","The Decryption for the Packet has failed");
	
	/** The error code. */
	public final String errorCode;
	
	/** The error message. */
	public final String errorMessage;

	/**
	 * Instantiates a new packet decryption failure exception constant.
	 *
	 * @param string1 the string 1
	 * @param string2 the string 2
	 */
	PacketDecryptionFailureExceptionConstant(String string1,String string2) {
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
