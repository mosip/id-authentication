package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public enum RPRPlatformErrorMessages {
	
	//Packet Receiver Error Messages
	PACKET_NOT_YET_SYNC("Registration packet is not in Sync with Sync table"), 
	PACKET_SIZE_GREATER_THAN_LIMIT("The Registration Packet Size has exceeded the Max Size Limit"), 
	INVALID_PACKET_FORMAT("Invalid packet format"),
	VALIDATION_EXCEPTION("Validation Exception"),
	DUPLICATE_PACKET_RECIEVED("The request received is a duplicate request to upload a Packet"),
	REGISTRATION_TABLE_NOT_ACCESSIBLE("The Registration Table is not accessible"),
	TRANSACTION_TABLE_NOT_ACCESSIBLE("Transaction table is not accessible"),
	CONNECTION_NOT_AVAILABLE("Connection to packet store not available"),
	INVALID_CONNECTION_PARAMETERS("Invalid connection parameter"),
	PACKET_NOT_AVAILABLE("Packet not available"),
	INVALID_CONNECTION_CREDENTIALS("Invalid connection credentials"),
	INVALID_PACKET_FILE_NAME("Invalid packet file name"),
	INVALID_CONNECTION_PATH("Invalid connection path"),
	CORE_KERNEL_NOT_RESPONDING("The Kernel Configuration Manager is not responding"),
	FILE_PATH_NOT_ACCESSIBLE("The Folder Path is not accessible"),
	FILE_NOT_FOUND_IN_DESTINATION("The File is not present in destination folder"),
	FILE_NOT_FOUND_IN_SOURCE("The File is not present in source folder"),
	FILE_SIZE_EXCEED_EXCEPTION("The File is size exceeded"),
	
	DEPLOYMENT_FAILURE("Deploymet Failure"),
	UNSUPPORTED_ENCODING("Unsupported Encoding"),
	CONFIGURATION_SERVER_FAILURE_EXCEPTION("Configuration Server Failure Exception"),
	
	RESULT_NOT_FOUND("Result not found"),
	INVALID_QC_USER_ID("QC User is null"),
	INVALID_REGISTRATION_ID("Registration Id is null"),
	STRUCTURAL_VALIDATION_FAILED("Structural Validation Failed"),
	
	UNEXCEPTED_EXCEPTION("Unexpected exception"), //System Exceptions
	BAD_GATEWAY("Bad Gateway"),
	SERVICE_UNAVAILABLE("Service Unavailable"),
	SERVER_ERROR("Internal Server Error"),
	TIMEOUT_EXCEPTION("Timeout Error");
	
	
	private final String errorMessage;

	private RPRPlatformErrorMessages(String errorMsg) {
		errorMessage = errorMsg;
	}
	
    public String getValue() {
        return this.errorMessage;
    }

}
