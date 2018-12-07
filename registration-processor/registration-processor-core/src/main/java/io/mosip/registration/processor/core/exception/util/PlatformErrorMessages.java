package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public enum PlatformErrorMessages {
	
	//Packet Receiver Exception error code and message
	RPR_PKR_PACKET_NOT_YET_SYNC(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "001","Registration packet is not in Sync with Sync table"), 
	
	RPR_PKR_PACKET_SIZE_GREATER_THAN_LIMIT(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "002","The Registration Packet Size has exceeded the Max Size Limit"), 
	
	RPR_PKR_INVALID_PACKET_FORMAT(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "003","Invalid packet format"),
	
	RPR_PKR_VALIDATION_EXCEPTION(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "004","Validation Exception"),
	
	RPR_PKR_DUPLICATE_PACKET_RECIEVED(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "005","The request received is a duplicate request to upload a Packet"),
	
	RPR_PKR_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "006","Packet not avaialble"),
	
	//Registration Status Exception error code and message
	RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "001","The Registration Table is not accessible in Registration Status"),
	
	RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "002","Transaction table is not accessible"),

	//Packet Info Storage Exception error code and message
	RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "001","The Registration Table is not accessible"),
	RPR_PIS_IDENTITY_NOT_FOUND(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "002","Identity field not found in DemographicInfo Json"),
	RPR_PIS_UNABLE_TO_INSERT_DATA(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "003","Unable to insert data in db for registration Id :"),
	RPR_PIS_FILE_NOT_FOUND_IN_DFS(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "004","File not found in DFS"),
	
	//File adaptor ceph Exception error code and message
	RPR_FAC_CONNECTION_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "001" ,"The connection Parameters to create a Packet Store connection are not Found"),
	
	RPR_FAC_INVALID_CONNECTION_PARAMETERS(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "002", "Invalid connection parameter to create a Packet Store connection"),
	
	RPR_FAC_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "003", "Cannot find the Registration Packet"),
	
	//Packet Manager Exception error code and message	
	RPR_PKM_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "002","The Folder Path is not accessible"),
	
	RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "003","The File is not present in destination folder"),
	
	RPR_PKM_FILE_NOT_FOUND_IN_SOURCE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "004", "The File is not present in source folder"),
	
	// Registration processor camel bridge Exception error code and message
	
	RPR_CMB_DEPLOYMENT_FAILURE(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "001","Deploymet Failure"),
	
	RPR_CMB_UNSUPPORTED_ENCODING(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "002","Unsupported Encoding"),
	
	RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "003","Configuration Server Failure Exception"),
	
	//Quality Checker Exception error code and message
	RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "001","The Registration Table is not accessible"),
	
	RPR_QCR_RESULT_NOT_FOUND(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "002","Result not found"),
	
	RPR_QCR_INVALID_QC_USER_ID(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "003","QC User is null"),
	
	RPR_QCR_INVALID_REGISTRATION_ID(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "004","Registration Id is null"),

	//Stages - Packet validator Exception error code and message
	STRUCTURAL_VALIDATION_FAILED("","Structural Validation Failed"),
	
	
	//Packet scanner job Exception error code and message		
	RPR_PSJ_DFS_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "002","The Packet store set by the System is not accessible"),
	
	RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "003","The Retry folder set by the System is not accessible"),
	
	RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "004","The Virus scanner set by the System is not accessible"),
	
	RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "005","There is no space available in retry folder to upload the packet"),
	
	RPR_PSJ_VIRUS_SCAN_FAILED(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "006","Virus scan is failed"),
	
	RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "007","The FTP folder set by the System is not accessible"),
	
	//packet decryption job Exception error code and message	
	RPR_PDJ_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "001", "Packet not available"),
	
	RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "002", "The File path set by the System is not accessible"),
	
	RPR_PDJ_PACKET_DECRYPTION_FAILURE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "003","The Decryption for the Packet has failed"),
	
	RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION(PlatformErrorConstants.RPR_REST_CLIENT_MODULE + "001","Unknown resource provided"),
	
	RPR_MVS_INVALID_FILE_REQUEST(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "001", "Invalid file requested"),
	
	RPR_MVS_FILE_NOT_PRESENT(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "002", "Requested file is not present"),
	
	RPR_MVS_INVALID_STATUS_UPDATE(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "003", "Invalid status update"),
	
	RPR_MVS_NO_ASSIGNED_RECORD(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "004", "No Assigned Record Found"),
	
	
	/*****System Exception*****/
	
	RPR_SYS_UNEXCEPTED_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "001","Unexpected exception"),
	RPR_SYS_BAD_GATEWAY(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "002", "Bad Gateway"),
	RPR_SYS_SERVICE_UNAVAILABLE(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "003", "Service Unavailable"),
	RPR_SYS_SERVER_ERROR(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "004", "Internal Server Error"),
	RPR_SYS_TIMEOUT_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "005", "Timeout Error"),
	
	RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "006","Error while mapping Identity Json"),
	RPR_SYS_INSTANTIATION_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "007","Error while creating object of JsonValue class"),
	RPR_SYS_NO_SUCH_FIELD_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "008","Could not find the field"),
	RPR_SYS_JSON_PARSING_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "009","Error while parsing Json"),
	RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "010","Error while converting inputstream to bytes"),
	RPR_SYS_PARSING_DATE_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "011","Error while parsing date ");
	
	/** The error message. */
	private final String errorMessage;
	
	/** The error code. */
	private final String errorCode;
       
	/**
	 * Instantiates a new platform error messages.
	 *
	 * @param errorCode the error code
	 * @param errorMsg the error msg
	 */
	private PlatformErrorMessages(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMessage = errorMsg;
	}
	
    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return this.errorMessage;
    }
    
    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getCode() {
    	return this.errorCode;
    }


}
