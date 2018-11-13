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
	TRANSACTION_TABLE_NOT_ACCESSIBLE("Transaction table not accessible");
	private final String errorMessage;

	private RPRPlatformErrorMessages(String errorMsg) {
		errorMessage = errorMsg;
	}
	
    public String getValue() {
        return this.errorMessage;
    }

}
