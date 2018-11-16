package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public enum RPRPlatformErrorMessages {
	
	/** The packet not yet sync. */
	PACKET_NOT_YET_SYNC("Registration packet is not in Sync with Sync table"), 
	
	/** The packet size greater than limit. */
	PACKET_SIZE_GREATER_THAN_LIMIT("The Registration Packet Size has exceeded the Max Size Limit"), 
	
	/** The invalid packet format. */
	INVALID_PACKET_FORMAT("Invalid packet format"),
	
	/** The validation exception. */
	VALIDATION_EXCEPTION("Validation Exception"),
	
	/** The duplicate packet recieved. */
	DUPLICATE_PACKET_RECIEVED("The request received is a duplicate request to upload a Packet"),
	
	/** The registration table not accessible. */
	REGISTRATION_TABLE_NOT_ACCESSIBLE("The Registration Table is not accessible"),
	
	/** The transaction table not accessible. */
	TRANSACTION_TABLE_NOT_ACCESSIBLE("Transaction table is not accessible"),
	
	/** The connection not available. */
	CONNECTION_NOT_AVAILABLE("Connection to packet store not available"),
	
	/** The invalid connection parameters. */
	INVALID_CONNECTION_PARAMETERS("Invalid connection parameter"),
	
	/** The packet not available. */
	PACKET_NOT_AVAILABLE("Packet not available"),
	
	/** The invalid connection credentials. */
	INVALID_CONNECTION_CREDENTIALS("Invalid connection credentials"),
	
	/** The invalid packet file name. */
	INVALID_PACKET_FILE_NAME("Invalid packet file name"),
	
	/** The invalid connection path. */
	INVALID_CONNECTION_PATH("Invalid connection path"),
	
	/** The core kernel not responding. */
	CORE_KERNEL_NOT_RESPONDING("The Kernel Configuration Manager is not responding"),
	
	/** The file path not accessible. */
	FILE_PATH_NOT_ACCESSIBLE("The Folder Path is not accessible"),
	
	/** The file not found in destination. */
	FILE_NOT_FOUND_IN_DESTINATION("The File is not present in destination folder"),
	
	/** The file not found in source. */
	FILE_NOT_FOUND_IN_SOURCE("The File is not present in source folder"),
	
	/** The file size exceed exception. */
	FILE_SIZE_EXCEED_EXCEPTION("The File is size exceeded"),
	
	/** The packet not found exception. */
	PACKET_NOT_FOUND_EXCEPTION("Packet Not Found in Packet Store"),
	
	/** The deployment failure. */
	DEPLOYMENT_FAILURE("Deploymet Failure"),
	
	/** The unsupported encoding. */
	UNSUPPORTED_ENCODING("Unsupported Encoding"),
	
	/** The configuration server failure exception. */
	CONFIGURATION_SERVER_FAILURE_EXCEPTION("Configuration Server Failure Exception"),
	
	/** The result not found. */
	RESULT_NOT_FOUND("Result not found"),
	
	/** The invalid qc user id. */
	INVALID_QC_USER_ID("QC User is null"),
	
	/** The invalid registration id. */
	INVALID_REGISTRATION_ID("Registration Id is null"),
	
	/** The structural validation failed. */
	STRUCTURAL_VALIDATION_FAILED("Structural Validation Failed"),
	
	/** The dfs not accessible. */
	DFS_NOT_ACCESSIBLE("Packet store is not accessible"),
	
	/** The retry folder not accessible. */
	RETRY_FOLDER_NOT_ACCESSIBLE("Retry folder is not accessible"),
	
	/** The virus scan folder not accessible. */
	VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE("Virus scanner folder is not accessible"),
	
	/** The space unavailable for retry folder. */
	SPACE_UNAVAILABLE_FOR_RETRY_FOLDER("Space unavailable for retry folder"),
	
	/** The virus scan failed. */
	VIRUS_SCAN_FAILED("Virus scan is failed"),
	
	/** The ftp folder not accessible. */
	FTP_FOLDER_NOT_ACCESSIBLE("FTP folder is not accessible"),
	
	/** The unexcepted exception. */
	UNEXCEPTED_EXCEPTION("Unexpected exception"), /** The bad gateway. */
 //System Exceptions
	BAD_GATEWAY("Bad Gateway"),
	
	/** The service unavailable. */
	SERVICE_UNAVAILABLE("Service Unavailable"),
	
	/** The server error. */
	SERVER_ERROR("Internal Server Error"),
	
	/** The timeout exception. */
	TIMEOUT_EXCEPTION("Timeout Error");
	
	/** The error message. */
	private final String errorMessage;

	/**
	 * Instantiates a new RPR platform error messages.
	 *
	 * @param errorMsg the error msg
	 */
	private RPRPlatformErrorMessages(String errorMsg) {
		errorMessage = errorMsg;
	}
	
    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return this.errorMessage;
    }

}
