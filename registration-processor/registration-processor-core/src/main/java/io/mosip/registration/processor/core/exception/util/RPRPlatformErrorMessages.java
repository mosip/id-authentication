package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public enum RPRPlatformErrorMessages {
	
	//Packet Receiver Error Messages
	PACKET_NOT_YET_SYNC("Registration packet is not in Sync with Sync table"), 
	PACKET_SIZE_GREATER_THAN_LIMIT("Packet size is greater than the limit"), 
	INVALID_PACKET_FORMAT("Invalid packet format"),
	DUPLICATE_PACKET_RECIEVED("Duplicate packet Received"),
	REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE("Registrtion table is not accessible"),
	TRANSACTION_TABLE_NOT_ACCESSIBLE("Transaction table not accessible"),
	CONNECTION_NOT_AVAILABLE("Connection to packet store not available"),
	INVALID_CONNECTION_PARAMETERS("Invalid connection parameter"),
	PACKET_NOT_AVAILABLE("Packet not available"),
	INVALID_CONNECTION_CREDENTIALS("Invalid connection credentials"),
	INVALID_PACKET_FILE_NAME("Invalid packet file name"),
	INVALID_CONNECTION_PATH("Invalid connection path");
	
	
	
	private final String errorMessage;

	private RPRPlatformErrorMessages(String errorMsg) {
		errorMessage = errorMsg;
	}
	
    public String getValue() {
        return this.errorMessage;
    }

}
