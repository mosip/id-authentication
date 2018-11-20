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
	
	RPR_PKR_TIMEOUT(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "007","Timeout Error"),

	RPR_PKR_UNEXCEPTED_ERROR(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "008","Unexpected exception"),

	
	//Registration Status Exception error code and message
	RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "001","The Registration Table is not accessible"),
	
	RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "002","Transaction table is not accessible"),

	//Packet Info Storage Exception error code and message
	RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "001","The Registration Table is not accessible"),
	
	//File adaptor ceph Exception error code and message
	RPR_FAC_CONNECTION_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "001" ,"Connection to packet store not available"),
	
	RPR_FAC_INVALID_CONNECTION_PARAMETERS(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "002", "Invalid connection parameter"),
	
	RPR_FAC_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "003", "Packet not available"),
	
	RPR_FAC_TIMEOUT(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "004","Timeout Error"),
	
	RPR_FAC_UNEXCEPTED_ERROR(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "005","Unexpected exception"),
	
	//Packet Manager Exception error code and message
	RPR_PKM_CORE_KERNEL_NOT_RESPONDING(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "001","The Kernel Configuration Manager is not responding"),
	
	RPR_PKM_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "002","The Folder Path is not accessible"),
	
	RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "003","The File is not present in destination folder"),
	
	RPR_PKM_FILE_NOT_FOUND_IN_SOURCE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "004", "The File is not present in source folder"),
	
	RPR_PKM_UNEXCEPTED_EXCEPTION(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "005","Unexpected exception"), /** The bad gateway. */

	RPR_PKM_BAD_GATEWAY(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "006", "Bad Gateway"),
	
	RPR_PKM_SERVICE_UNAVAILABLE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "007", "Service Unavailable"),
	
	RPR_PKM_SERVER_ERROR(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "008", "Internal Server Error"),
	
	RPR_PKM_TIMEOUT_EXCEPTION(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "009", "Timeout Error"),
	
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
	RPR_PSJ_CORE_KERNEL_NOT_RESPONDING(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "001","The Kernel Configuration Manager is not responding"),
	
	RPR_PSJ_DFS_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "002","Packet store is not accessible"),
	
	RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "003","Retry folder is not accessible"),
	
	RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "004","Virus scanner folder is not accessible"),
	
	RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "005","Space unavailable for retry folder"),
	
	RPR_PSJ_VIRUS_SCAN_FAILED(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "006","Virus scan is failed"),
	
	RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "007","FTP folder is not accessible"),
	
	//packet decryption job Exception error code and message	
	RPR_PDJ_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "001", "Packet not available"),
	
	RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "002", "File path is no accessible"),
	
	RPR_PDJ_PACKET_DECRYPTION_FAILURE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "003","The Decryption for the Packet has failed");
	
	
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
