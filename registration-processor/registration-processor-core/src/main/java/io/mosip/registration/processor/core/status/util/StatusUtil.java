package io.mosip.registration.processor.core.status.util;

public enum StatusUtil {
	//Packet Receiver Stage
	PACKET_RECEIVED(StatusConstants.PACKET_RECEIVER_MODULE_SUCCESS + "001" , "Packet has reached Packet Receiver"),
	PACKET_UPLOADED_TO_LANDING_ZONE(StatusConstants.PACKET_RECEIVER_MODULE_SUCCESS + "002" , "Packet is Uploaded to Landing Zone"),
	VIRUS_SCANNER_FAILED(StatusConstants.PACKET_RECEIVER_MODULE_FAILURE + "001" , "Packet is Virus Infected"),
	PACKET_DECRYPTION_FAILED(StatusConstants.PACKET_RECEIVER_MODULE_FAILURE + "002" , "Packet Decryption Failed"),
	
	//Packet uploader stage
	PACKET_UPLOADED(StatusConstants.PACKET_UPLOADER_MODULE_SUCCESS + "001","Packet is Uploaded to Packet Store"),
	PACKET_CLEANUP_FAILED(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "001","Packet Clean Up Failed from Landing Zone"),
	PACKET_ARCHIVAL_FAILED(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "002" , "Packet Archival Failed"),
	PACKET_UPLOAD_FAILED(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "003" , "Packet Upload Failed"),
	PACKET_NOT_FOUND_LANDING_ZIONE(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "004" , "Packet Not Found in Landing Zone"),
	PACKET_HASHCODE_VALIDATION_FAILED(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "005" , "Packet Hash Code Validation Failed"),
	VIRUS_SCANNER_FAILED_UPLOADER(StatusConstants.PACKET_UPLOADER_MODULE_FAILED + "006" , "Packet is Virus Infected"),

	//Quality checker stage
	INDIVIDUAL_BIOMETRIC_NOT_FOUND(StatusConstants.QUALITY_CHECKER_MODULE_SUCCESS + "001", "Individual Biometric Parameter Not Found in ID JSON"),
	BIOMETRIC_QUALITY_CHECK_SUCCESS(StatusConstants.QUALITY_CHECKER_MODULE_SUCCESS + "002" , "Biometric Quality Check is Successful"),
	BIOMETRIC_QUALITY_CHECK_FAILED(StatusConstants.QUALITY_CHECKER_MODULE_FAILED + "001" , "Quality Score of Biometrics Captured is Below the Threshold"),
	
	//packet validator stage
	PACKET_STRUCTURAL_VALIDATION_SUCCESS(StatusConstants.PACKET_VALIDATOR_MODULE_SUCCESS + "001" , "Packet Validation is Successful"),
	FILE_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "001", "File Validation Failed"),
	SCHEMA_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "002" , "Schema Validation Failed"),
	CHECKSUM_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "003" , "Check Sum Validation Failed"),
	INDIVIDUAL_BIOMETRIC_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "004" , "Individual Biometric Validation Failed"),
	APPLICANT_DOCUMENT_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "005" , "Applicant Document Validation Failed"),
	MASTER_DATA_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "006" , "Master Data Validation Failed"),
	ACTIVATE_DEACTIVATE_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "007" , "Packet Validation for Activate/Deactivate Packet Failed"),
	UIN_NOT_FOUND_IDREPO(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "008" , "UIN is Not Found in ID Repository"),
	MANDATORY_VALIDATION_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "009" , "Mandatory Fields are Not Present in ID Object"),
	RID_AND_TYPE_SYNC_FAILED(StatusConstants.PACKET_VALIDATOR_MODULE_FAILED + "010" , "RID & Type not matched from sync table"),
	
	//External stage
	EXTERNAL_STAGE_SUCCESS(StatusConstants.EXTERNAL_SATGE_MODULE_SUCCESS + "001" , "Packet processing in External stage is sucessful"),
	EXTERNAL_STAGE_FAILED(StatusConstants.EXTERNAL_SATGE_MODULE_SUCCESS + "001" , "Packet processing in External stage failed"),

	//OSI Validator  stage
	//1.UMC Validator stage
	GPS_DETAILS_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "001" , "GPS Details are Not Found in Packet"),
	CENTER_ID_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "002" , "Center ID Not Found in Master DB - "),
	CENTER_ID_INACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "003" , "Center was InActive during Packet Creation - "),
	MACHINE_ID_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "004" , "Machine ID Not Found in Master DB - "),
	MACHINE_ID_NOT_ACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "005" , "Machine ID was InActive during Packet Creation - "),
	SUPERVISOR_OFFICER_NOT_ACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "006" , "SupervisorId and OfficerId are inActive"),
	CENTER_DEVICE_MAPPING_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "007" , "Center-Device Mapping Not Found - "),
	CENTER_DEVICE_MAPPING_INACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "008" , "Center-Device Mapping was InActive during Packet Creation - "),
    DEVICE_NOT_FOUND_MASTER_DB(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "009" , "Device Not Found in Master DB - "),
    DEVICE_ID_INACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "010" , "Device ID was InActive during Packet Creation - "),
    PACKET_CREATION_WORKING_HOURS(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "011" , "Packet was Not Created during Working Hours - "),
    REGISTRATION_CENTER_TIMESTAMP_FAILURE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "012" , "Registration Center timestamp failed"),
    FAILED_TO_GET_MACHINE_DETAIL(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "013" , "Failed to Get machine id details "),
    FAILED_TO_GET_CENTER_DETAIL(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "014" , "Failed to Get center id details "),
    PACKET_IS_ON_HOLD(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "015" , "Packet is on Hold due to parent packet processing"),
    
    SUPERVISOR_OFFICER_NOT_FOUND_PACKET(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "012" , "Both Officer and Supervisor IDs are NULL"),
    SUPERVISOR_OR_OFFICER_WAS_INACTIVE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "013" , "Officer or Supervisor was Not Active during Packet Creation - "),
    PACKET_CREATION_DATE_NOT_FOUND_IN_PACKET(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "014" , "Packet Creation Date is NULL"),
    PASSWORD_OTP_FAILURE(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "015" , "Password or OTP Verification Failed for Officer - "),
    OFFICER_SUPERVISOR_AUTHENTICATION_FAILED(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "016" , "Officer or Supervisor Biometric Authentication Failed - "),
    PASSWORD_OTP_FAILURE_SUPERVISOR(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "017" , "Password or OTP Verification Failed for Supervisor - "),
    UIN_RID_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "018" , "UIN or RID of Parent Not Found in Packet"),
    PARENT_UIN_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "019" , "Parent UIN not Found for the Given RID"),
   PARENT_BIOMETRIC_FILE_NAME_NOT_FOUND(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "020" , "Parent Biometric File Name Not Found"),
   PACKET_ON_HOLD(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "021" , "Packet On-Hold as Parent RID Not Found"),
   CHILD_PACKET_REJECTED(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "022" , "Packet Rejected as Parent Packet is Rejected"),
	MACHINE_ID_NOT_FOUND_MASTER_DB(StatusConstants.OSI_VALIDAOR_MODULE_FAILED + "023" , "MachineId not found in master db - "),
   OSI_VALIDATION_SUCCESS(StatusConstants.OSI_VALIDAOR_MODULE_SUCCESS + "001" , "OSI Validation is Successful"),
   
   //Message sender stage
   NOTIFICATION_SUCESSFUL(StatusConstants.OMESSAGE_SENDER_MODULE_SUCCESS + "001" , "Notification Sent Successfully"),
   TEMPLATE_CONFIGURATION_NOT_FOUND(StatusConstants.OMESSAGE_SENDER_MODULE_FAILED + "001" , "Template configuration and language not found"),
   EMAIL_PHONE_TEMPLATE_NOTIFICATION_MISSING(StatusConstants.OMESSAGE_SENDER_MODULE_FAILED + "002" , "Email ID or Phone or Template or Notification Type is Missing"),
  
   //printing stage
   PDF_ADDED_TO_QUEUE_SUCCESS(StatusConstants.PRINT_STAGE_MODULE_SUCCESS + "001" , "PDF is added to Queue for Printing"),
   PDF_ADDED_TO_QUEUE_FAILED(StatusConstants.PRINT_STAGE_MODULE_FAILED + "001" , "PDF was not added to Queue due to Queue Failure"),
   PRINT_POST_COMPLETED(StatusConstants.PRINT_STAGE_MODULE_SUCCESS + "002" , "Printing and Post Completed"),
   RESEND_UIN_CARD(StatusConstants.PRINT_STAGE_MODULE_SUCCESS + "003" , "Re-Sent UIN Card for Printing"),
  
   //Abis middleware stage
   INSERT_IDENTIFY_REQUEST_SUCCESS(StatusConstants.ABIS_MIDDLEWARE_MODULE_SUCCESS + "001", "Insert or Identify Request sent to ABIS Queue is succesful"),
   INSERT_IDENTIFY_REQUEST_FAILED(StatusConstants.ABIS_MIDDLEWARE_MODULE_FAILED + "001", "Insert or Identify Request sent to ABIS Queue is Unsuccesful"),
   INSERT_IDENTIFY_RESPONSE_SUCCESS(StatusConstants.ABIS_MIDDLEWARE_MODULE_SUCCESS + "002", "Recived sucessful response from ABIS"),
   INSERT_IDENTIFY_RESPONSE_FAILED(StatusConstants.ABIS_MIDDLEWARE_MODULE_SUCCESS + "002", "Received failed response from ABIS - "),

   //System Exceptions
   //Bio dedupe stage
   BIO_DEDUPE_INPROGRESS(StatusConstants.BIO_DEDUPE_MODULE_SUCCESS + "001", "Biometric Deduplication In-Progress"),
   BIO_DEDUPE_SUCCESS(StatusConstants.BIO_DEDUPE_MODULE_SUCCESS + "002", "Biometric Deduplication is Successful"),
   BIO_DEDUPE_POTENTIAL_MATCH(StatusConstants.BIO_DEDUPE_MODULE_FAILED + "001", "Potential Biometric Match Found while Processing Packet"),
   LOST_PACKET_BIOMETRICS_NOT_FOUND(StatusConstants.BIO_DEDUPE_MODULE_FAILED + "002", "No Match was Found for the Biometrics Received"),
   LOST_PACKET_UNIQUE_MATCH_FOUND(StatusConstants.BIO_DEDUPE_MODULE_SUCCESS + "003", "Unique Match was Found for the Biometrics Received"),
   LOST_PACKET_MULTIPLE_MATCH_FOUND(StatusConstants.BIO_DEDUPE_MODULE_FAILED + "003", "Multiple Match was Found for the Biometrics Received"),

   //Biometric authentication stage
   BIOMETRIC_AUTHENTICATION_FAILED(StatusConstants.BIO_METRIC_AUTHENTICATION_MODULE_FAILED+"001","Biometric Authentication has Failed"), 
	BIOMETRIC_AUTHENTICATION_SUCCESS(StatusConstants.BIO_METRIC_AUTHENTICATION_MODULE_SUCCESS+"001","Biometric Authentication is Successful"),
	BIOMETRIC_FILE_NOT_FOUND(StatusConstants.SYSTEM_EXCEPTION_CODE , "Biometric File Not Found"),
	BIOMETRIC_AUTHENTICATION_FAILED_FILE_NOT_FOUND(StatusConstants.SYSTEM_EXCEPTION_CODE , "Biometric Authentication Failed File is not present"),
	INDIVIDUAL_BIOMETRIC_AUTHENTICATION_FAILED(StatusConstants.BIO_METRIC_AUTHENTICATION_MODULE_FAILED+"001","Individual authentication failed"),

   //Demo dedupe stage
   DEMO_DEDUPE_SUCCESS(StatusConstants.DEMO_DEDUPE_MODULE_SUCCESS + "001" , "Demo Dedupe is Successful"),
   POTENTIAL_MATCH_FOUND_IN_ABIS(StatusConstants.DEMO_DEDUPE_MODULE_FAILED + "001" , "Biometric Duplicate was Found in ABIS"),
   POTENTIAL_MATCH_FOUND(StatusConstants.DEMO_DEDUPE_MODULE_FAILED + "002" , "Potential Demo Match was Found"),
  //Manual verification stage
   MANUAL_VERIFIER_APPROVED_PACKET(StatusConstants.MANUAL_VERIFICATION_MODULE_SUCCESS + "001" , "Match Not Found by Manual Verifier"),
   MANUAL_VERIFIER_REJECTED_PACKET(StatusConstants.MANUAL_VERIFICATION_MODULE_FAILED + "001" , "Match Found by Manual Verifier"),

   //Uin generator stage
   UIN_GENERATED_SUCCESS(StatusConstants.UIN_GENERATOR_MODULE_SUCCESS + "001" , "UIN Generated Successfully"),
   UIN_DATA_UPDATION_SUCCESS(StatusConstants.UIN_GENERATOR_MODULE_SUCCESS + "002" , "UIN Data is Updated Successfully"),
   UIN_ACTIVATED_SUCCESS(StatusConstants.UIN_GENERATOR_MODULE_SUCCESS + "003" , "UIN is Activated"),
   UIN_DEACTIVATION_SUCCESS(StatusConstants.UIN_GENERATOR_MODULE_SUCCESS + "004" , "UIN is Deactivated"),
   LINK_RID_FOR_LOST_PACKET_SUCCESS(StatusConstants.UIN_GENERATOR_MODULE_SUCCESS + "005" , "RID linked Successfully for Lost UIN Packet"),



   UIN_ALREADY_ACTIVATED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "001" , "UIN is already Activated"),
   UIN_ACTIVATED_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "002" , "UIN Activation Failed"),
   UIN_ALREADY_DEACTIVATED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "003" , "UIN already deactivated"),

   UIN_GENERATION_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "004" , "UIN Generation failed - "),
   UIN_DATA_UPDATION_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "005" , "UIN Updation failed - "),
   UIN_REACTIVATION_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "006" , "UIN Reactivation  failed - "),
  UIN_DEACTIVATION_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "007" , "UIN Deactivation  failed - "),
  LINK_RID_FOR_LOST_PACKET_FAILED(StatusConstants.UIN_GENERATOR_MODULE_FAILED + "008" , "UIn not found the the matched RID"),
  //System Exceptions
	VIRUS_SCANNER_SERVICE_NOT_ACCESSIBLE(StatusConstants.SYSTEM_EXCEPTION_CODE , "Virus Scanner Service is not accessible"),
	DB_NOT_ACCESSIBLE(StatusConstants.SYSTEM_EXCEPTION_CODE , "Databse Not Accessible"),
	PACKET_NOT_FOUND_PACKET_STORE(StatusConstants.SYSTEM_EXCEPTION_CODE , "Packet not found in File System"),
	FS_ADAPTER_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "FS Adapter Exception Occurred"),
	JSCH_EXCEPTION_OCCURED(StatusConstants.SYSTEM_EXCEPTION_CODE , "JSCH Connection Exception Occurred"),
	SFTP_FILE_OPERATION_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "SFTP File Operation Exception Occurred"),
	IO_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "IO Exception Occurred"),
	BIO_METRIC_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "Biometric Exception Occurred in IDA "),
	BIO_METRIC_FILE_MISSING(StatusConstants.SYSTEM_EXCEPTION_CODE , "Applicant biometric fileName/file is missing"),
	
	
	UNKNOWN_EXCEPTION_OCCURED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Unknown exception occured "),
	API_RESOUCE_ACCESS_FAILED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Unable to access API resource" ),
    JSON_PARSING_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "Error Occurred while Parsing JSON"),
    BASE_CHECKED_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "Packet validation failed "),
    BASE_UNCHECKED_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "Packet validation failed "),
    
    
    OFFICER_AUTHENTICATION_FAILED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Officer Authentication Failed: "),
    SUPERVISOR_AUTHENTICATION_FAILED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Supervisor Authentication Failed: "),
    
    IDENTIFY_RESPONSE_FAILED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Identify Response Failed for Request ID - "),
    INSERT_RESPONSE_FAILED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Insert Response Failed for Request ID - "),
    SYSTEM_EXCEPTION_OCCURED(StatusConstants.SYSTEM_EXCEPTION_CODE , "Internal error occured - "),

    CBEF_NOT_FOUND(StatusConstants.SYSTEM_EXCEPTION_CODE , "Unable to Find Applicant CBEFF for Adult"),

    IIEGAL_ARGUMENT_EXCEPTION(StatusConstants.SYSTEM_EXCEPTION_CODE , "Illegal Argument Exception Occurred - "),
    DEMO_DEDUPE_FAILED_IN_ABIS(StatusConstants.SYSTEM_EXCEPTION_CODE , "Demo Dedupe Failed  in ABIS"),
    RE_PROCESS_FAILED(StatusConstants.RE_PROCESS_MODULE_FAILED+"001","Reprocess count has exceeded the configured attempts"),
	RE_PROCESS_COMPLETED(StatusConstants.RE_PROCESS_MODULE_SUCCESS+"001","Reprocess Completed");

	private final String statusComment;
	private final String statusCode;
	
	private StatusUtil(String statusCode, String statusComment) {
		this.statusCode = statusCode;
		this.statusComment = statusComment;
	}

	public String getMessage() {
		return this.statusComment;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getCode() {
		return this.statusCode;
	}

}
