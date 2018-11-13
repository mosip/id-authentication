package io.mosip.registration.processor.packet.receiver.exception.utils;

public enum RPRErrorMessage {

	//Packet Receiver Error Messages
	PACKET_NOT_YET_SYNC("Registration packet is not in Sync with Sync table"), 
	PACKET_SIZE_GREATER_THAN_LIMIT("Packet size is greater than the limit"), 
	INVALID_PACKET_FORMAT("Invalid packet format"),
	DUPLICATE_PACKET_RECIEVED("Duplicate packet Received");

	private final String errorMessage;

	private RPRErrorMessage(String errorMsg) {
		errorMessage = errorMsg;
	}
	
    public String getValue() {
        return this.errorMessage;
    }
    
}
