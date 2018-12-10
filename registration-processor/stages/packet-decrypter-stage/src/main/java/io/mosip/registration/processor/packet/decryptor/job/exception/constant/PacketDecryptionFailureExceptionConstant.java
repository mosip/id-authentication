package io.mosip.registration.processor.packet.decryptor.job.exception.constant;

public enum PacketDecryptionFailureExceptionConstant {
	MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE("RPR-PKD-004","The Decryption for the Packet has failed");
	
	public final String errorCode;
	public final String errorMessage;

	PacketDecryptionFailureExceptionConstant(String string1,String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}
