package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public enum PlatformErrorMessages {

	/** The rpr pkr packet not yet sync. */
	// Packet Receiver Exception error code and message
	RPR_PKR_PACKET_NOT_YET_SYNC(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "001",
			"Registration packet is not in Sync with Sync table"),

	RPR_PKR_INVALID_PACKET_SIZE(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "002",
			"The Registration Packet Size is invalid"),

	/** The rpr pkr invalid packet format. */
	RPR_PKR_INVALID_PACKET_FORMAT(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "003", "Invalid packet format"),

	/** The rpr pkr validation exception. */
	RPR_PKR_VALIDATION_EXCEPTION(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "004", "Validation Exception"),

	/** The rpr pkr duplicate packet recieved. */
	RPR_PKR_DUPLICATE_PACKET_RECIEVED(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "005",
			"The request received is a duplicate request to upload a Packet"),

	/** The rpr pkr packet not available. */
	RPR_PKR_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_PACKET_RECEIVER_MODULE + "006", "Packet not avaialble"),

	/** The rpr rgs registration table not accessible. */
	// Registration Status Exception error code and message
	RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "001",
			"The Registration Table is not accessible in Registration Status"),

	/** The rpr rgs transaction table not accessible. */
	RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "002",
			"Transaction table is not accessible"),

	/** The rpr rgs invalid synctype. */
	RPR_RGS_INVALID_SYNCTYPE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "003",
			"Invalid syncType. Available types are NEW, CORRECTION, UPDATE, LOST_UIN, UPDATE_UIN, ACTIVATE_UIN, DEACTIVATE_UIN"),

	/** The rpr rgs invalid languagecode. */
	RPR_RGS_INVALID_LANGUAGECODE(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "004",
			"Language Code must be of three character"),

	/** The rpr rgs invalid regid parentregid. */
	RPR_RGS_INVALID_REGID_PARENTREGID(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "005",
			"RegistrationId and Parent RegistrationId cannot be same"),

	/** The rpr rgs empty registrationid. */
	RPR_RGS_EMPTY_REGISTRATIONID(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "006",
			"RegistrationId cannot be null"),

	/** The rpr rgs invalid registrationid timestamp. */
	RPR_RGS_INVALID_REGISTRATIONID_TIMESTAMP(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "007",
			"Invalid Time Stamp Found in RegistrationId"),

	/** The rpr rgs invalid registrationid. */
	RPR_RGS_INVALID_REGISTRATIONID(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "008",
			"RegistrationId Must Be Numeric Only"),

	/** The rpr rgs invalid registrationid length. */
	RPR_RGS_INVALID_REGISTRATIONID_LENGTH(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "009",
			"RegistrationId Length Must Be 29"),

	/** The rpr rgs invalid prid timestamp. */
	RPR_RGS_INVALID_PRID_TIMESTAMP(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "010",
			"Invalid Time Stamp Found in Parent RegistrationId"),

	/** The rpr rgs invalid prid. */
	RPR_RGS_INVALID_PRID(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "011",
			"Parent RegistrationId Must Be Numeric Only"),

	/** The rpr rgs invalid prid length. */
	RPR_RGS_INVALID_PRID_LENGTH(PlatformErrorConstants.RPR_REGISTRATION_STATUS_MODULE + "012",
			"Parent RegistrationId Length Must Be 29"),

	/** The rpr pis registration table not accessible. */
	// Packet Info Storage Exception error code and message
	RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "001",
			"The Registration Table is not accessible"),
	/** The rpr pis identity not found. */
	RPR_PIS_IDENTITY_NOT_FOUND(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "002",
			"Identity field not found in DemographicInfo Json"),
	/** The rpr pis unable to insert data. */
	RPR_PIS_UNABLE_TO_INSERT_DATA(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "003",
			"Unable to insert data in db for registration Id :"),
	/** The rpr pis file not found in dfs. */
	RPR_PIS_FILE_NOT_FOUND_IN_DFS(PlatformErrorConstants.RPR_PACKET_INFO_STORAGE_MODULE + "004",
			"File not found in DFS"),

	/** The rpr fac connection not available. */
	// File adaptor ceph Exception error code and message
	RPR_FAC_CONNECTION_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "001",
			"The connection Parameters to create a Packet Store connection are not Found"),

	/** The rpr fac invalid connection parameters. */
	RPR_FAC_INVALID_CONNECTION_PARAMETERS(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "002",
			"Invalid connection parameter to create a Packet Store connection"),

	/** The rpr fac packet not available. */
	RPR_FAC_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE + "003",
			"Cannot find the Registration Packet"),

	/** The rpr pkm file path not accessible. */
	// Packet Manager Exception error code and message
	RPR_PKM_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "002",
			"The Folder Path is not accessible"),

	/** The rpr pkm file not found in destination. */
	RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "003",
			"The File is not present in destination folder"),

	/** The rpr pkm file not found in source. */
	RPR_PKM_FILE_NOT_FOUND_IN_SOURCE(PlatformErrorConstants.RPR_PACKET_MANAGER_MODULE + "004",
			"The File is not present in source folder"),

	// Registration processor camel bridge Exception error code and message

	/** The rpr cmb deployment failure. */
	RPR_CMB_DEPLOYMENT_FAILURE(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "001", "Deploymet Failure"),

	/** The rpr cmb unsupported encoding. */
	RPR_CMB_UNSUPPORTED_ENCODING(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "002", "Unsupported Encoding"),

	/** The rpr cmb configuration server failure exception. */
	RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "003",
			"Configuration Server Failure Exception"),
	
	/** The rpr cmb malformed cluster manager url exception. */
	RPR_CMB_MALFORMED_URL_EXCEPTION(PlatformErrorConstants.RPR_CAMEL_BRIDGE_MODULE + "004", "Malformed Cluster Manager Url Exception"),
	
	/** The rpr qcr registration table not accessible. */
	// Quality Checker Exception error code and message
	RPR_QCR_REGISTRATION_TABLE_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "001",
			"The Registration Table is not accessible"),

	/** The rpr qcr result not found. */
	RPR_QCR_RESULT_NOT_FOUND(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "002", "Result not found"),

	/** The rpr qcr invalid qc user id. */
	RPR_QCR_INVALID_QC_USER_ID(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "003", "QC User is null"),

	/** The rpr qcr invalid registration id. */
	RPR_QCR_INVALID_REGISTRATION_ID(PlatformErrorConstants.RPR_QUALITY_CHECKER_MODULE + "004",
			"Registration Id is null"),

	PACKET_UPLOAD_FAILED("", "Packet Upload failed"),

	/** The structural validation failed. */
	// Stages - Packet validator Exception error code and message
	STRUCTURAL_VALIDATION_FAILED("", "Structural Validation Failed"),

	// UIN check - JSON file encoding failed.
	UNSUPPORTED_ENCODING("", "json object parsing failed"),

	/** The osi validation failed. */
	// Stages - OSI Exception error code and message
	OSI_VALIDATION_FAILED("", "OSI Validation Failed"),

	/** The packet demo dedupe failed. */
	// Stages - Demo-Dedupe error code and message
	PACKET_DEMO_DEDUPE_FAILED("", "Demo dedupe Failed"),

	/** The packet bio dedupe failed. */
	// Stages - Bio-Dedupe error code and message
	PACKET_BIO_DEDUPE_FAILED("", "Bio dedupe Failed"),

	// Stages - Packet-Validator error message
	REVERSE_DATA_SYNC_FAILED("", "Reverse data sync failed"),

	/** The rpr psj dfs not accessible. */
	// Packet scanner job Exception error code and message
	RPR_PSJ_DFS_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "002",
			"The Packet store set by the System is not accessible"),

	/** The rpr psj retry folder not accessible. */
	RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "003",
			"The Retry folder set by the System is not accessible"),

	/** The rpr psj virus scan folder not accessible. */
	RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "004",
			"The Virus scanner set by the System is not accessible"),

	/** The rpr psj space unavailable for retry folder. */
	RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "005",
			"There is no space available in retry folder to upload the packet"),

	/** The rpr psj virus scan failed. */
	RPR_PSJ_VIRUS_SCAN_FAILED(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "006", "Virus scan is failed"),

	/** The rpr psj ftp folder not accessible. */
	RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_SCANNER_JOB_MODULE + "007",
			"The FTP folder set by the System is not accessible"),

	/** The rpr pdj packet not available. */
	// packet decryption job Exception error code and message
	RPR_PDJ_PACKET_NOT_AVAILABLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "001",
			"Packet not available"),

	/** The rpr pdj file path not accessible. */
	RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "002",
			"The File path set by the System is not accessible"),

	/** The rpr pdj packet decryption failure. */
	RPR_PDJ_PACKET_DECRYPTION_FAILURE(PlatformErrorConstants.RPR_PACKET_DECRYPTION_JOB_MODULE + "003",
			"The Decryption for the Packet has failed"),

	/** The rpr rct unknown resource exception. */
	RPR_RCT_UNKNOWN_RESOURCE_EXCEPTION(PlatformErrorConstants.RPR_REST_CLIENT_MODULE + "001",
			"Unknown resource provided"),

	/** The rpr mvs invalid file request. */
	RPR_MVS_INVALID_FILE_REQUEST(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "001",
			"Invalid file requested"),

	/** The rpr mvs file not present. */
	RPR_MVS_FILE_NOT_PRESENT(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "002",
			"Requested file is not present"),

	/** The rpr mvs invalid status update. */
	RPR_MVS_INVALID_STATUS_UPDATE(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "003",
			"Invalid status update"),

	RPR_MVS_INVALID_FIELD(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "005",
			" fields can not be empty"),

	
	/** The rpr mvs no assigned record. */
	RPR_MVS_NO_ASSIGNED_RECORD(PlatformErrorConstants.RPR_MANUAL_VERIFICATION_MODULE + "004",
			"No Assigned Record Found"),

	/** The rpr tem not found. */
	// Registration processor Message sender Exception error code
	RPR_TEM_NOT_FOUND(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "001", "Template was Not Found"),

	/** The rpr tem processing failure. */
	RPR_TEM_PROCESSING_FAILURE(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "002",
			"The Processing of Template Failed "),

	/** The rpr sms template generation failure. */
	RPR_SMS_TEMPLATE_GENERATION_FAILURE(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "001",
			"Template Generation failed"),

	/** The rpr sms phone number not found. */
	RPR_SMS_PHONE_NUMBER_NOT_FOUND(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "002",
			"Phone number was not found"),

	/** The rpr eml emailid not found. */
	RPR_EML_EMAILID_NOT_FOUND(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "001", "Email Id was not found"),

	/** The rpr tem configuration not found. */
	RPR_TEM_CONFIGURATION_NOT_FOUND(PlatformErrorConstants.RPR_MESSAGE_SENDER_TEMPLATE + "003",
			"The Configuration and Language code not found"),

	/** The rpr pum packet not found exception. */
	RPR_PUM_PACKET_NOT_FOUND_EXCEPTION(PlatformErrorConstants.RPR_PACKET_UPLOADER_MODULE + "001",
			"Packet Not Found in Packet Store"),

	/** The rpr pum packet deletion info. */
	RPR_PUM_PACKET_DELETION_INFO(PlatformErrorConstants.RPR_PACKET_UPLOADER_MODULE + "002",
			"File is Already exists in DFS location And its now Deleted from Virus scanner job"),

	/** The rpr bdd abis internal error. */
	RPR_BDD_ABIS_INTERNAL_ERROR(PlatformErrorConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "001",
			"ABIS for the Reference ID and Request ID threw an Internal Error"),

	/** The rpr bdd abis abort. */
	RPR_BDD_ABIS_ABORT(PlatformErrorConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "002",
			"ABIS for the Reference ID and Request ID was Abort"),

	/** The rpr bdd unexcepted error. */
	RPR_BDD_UNEXCEPTED_ERROR(PlatformErrorConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "003",
			"ABIS for the Reference ID and Request ID was Not able to Access Biometric Data"),

	/** The rpr bdd unable to serve request. */
	RPR_BDD_UNABLE_TO_SERVE_REQUEST(PlatformErrorConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "004",
			"ABIS for the Reference ID and Request ID was Unable to Execute the Request"),

	/** *** System Exception ****. */

	RPR_SYS_UNEXCEPTED_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "001", "Unexpected exception"),

	/** The rpr sys bad gateway. */
	RPR_SYS_BAD_GATEWAY(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "002", "Bad Gateway"),

	/** The rpr sys service unavailable. */
	RPR_SYS_SERVICE_UNAVAILABLE(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "003", "Service Unavailable"),

	/** The rpr sys server error. */
	RPR_SYS_SERVER_ERROR(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "004", "Internal Server Error"),

	/** The rpr sys timeout exception. */
	RPR_SYS_TIMEOUT_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "005", "Timeout Error"),

	/** The rpr sys identity json mapping exception. */
	RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "006",
			"Error while mapping Identity Json"),

	/** The rpr sys instantiation exception. */
	RPR_SYS_INSTANTIATION_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "007",
			"Error while creating object of JsonValue class"),

	/** The rpr sys no such field exception. */
	RPR_SYS_NO_SUCH_FIELD_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "008", "Could not find the field"),

	/** The rpr sys json parsing exception. */
	RPR_SYS_JSON_PARSING_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "009", "Error while parsing Json"),

	/** The rpr sys unable to convert stream to bytes. */
	RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "010",
			"Error while converting inputstream to bytes"),

	/** The rpr sys parsing date exception. */
	RPR_SYS_PARSING_DATE_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "011", "Error while parsing date "),

	/** The rpr sys io exception. */
	RPR_SYS_IO_EXCEPTION(PlatformErrorConstants.RPR_SYSTEM_EXCEPTION + "012", "IO EXCEPTION "),
	
	//Printing stage exceptions
	RPR_PRT_PDF_NOT_GENERATED(PlatformErrorConstants.RPR_PRINTING_MODULE + "001", "Error while generating PDF for UIN Card"),
	
	RPR_PRT_UIN_NOT_FOUND_IN_DATABASE(PlatformErrorConstants.RPR_PRINTING_MODULE + "002", "UIN not found in database"),
	
	RPR_PRT_PDF_GENERATION_FAILED(PlatformErrorConstants.RPR_PRINTING_MODULE + "003", "PDF Generation Failed");
	
	/** The error message. */
	private final String errorMessage;

	/** The error code. */
	private final String errorCode;

	/**
	 * Instantiates a new platform error messages.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMsg
	 *            the error msg
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
