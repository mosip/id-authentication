package io.mosip.registration.processor.packet.archiver.util.exception.constant;

public enum PacketNotFoundExceptionConstant {
	PACKET_NOT_FOUND_ERROR("RER-ARC-001", "Packet Not Found in DFS");

	public final String errorCode;
	public final String errorMessage;

	PacketNotFoundExceptionConstant(String string1, String string2) {
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
