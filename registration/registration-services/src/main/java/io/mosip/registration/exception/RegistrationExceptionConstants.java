package io.mosip.registration.exception;

import io.mosip.registration.constants.RegistrationConstants;

import static io.mosip.registration.constants.RegistrationConstants.PACKET_CREATION_EXP_CODE;
import static io.mosip.registration.constants.RegistrationConstants.PACKET_UPLOAD_EXP_CODE;
import static io.mosip.registration.constants.RegistrationConstants.REG_ACK_EXP_CODE;

/**
 * Exception enum for Registration Processor Module
 * 
 * @author Balaji.Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationExceptionConstants {

	REG_IO_EXCEPTION(PACKET_CREATION_EXP_CODE + "ZCM-001", "IO exception"),

	// PacketHandlerService
	REG_PACKET_CREATION_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PHS-001", "The packet zip file is either null or empty"),

	// PacketCreationService
	REG_JSON_PROCESSING_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-001", "Exception while parsing object to JSON"),
	REG_PACKET_CREATION_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-002", "Unable to create packet zip from RegistrationDTO"),
	REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PCS-002", "Exception while creating CBEFF file for biometrics"),

	// AuditDAO
	REG_GET_AUDITS_EXCEPTION(PACKET_CREATION_EXP_CODE + "ADA-001", "Unable to fetch Audit Logs based on provided dates"),

	// PacketMetaInfoConverter
	REG_PACKET_METAINFO_CONVERSION_EXCEPTION(PACKET_CREATION_EXP_CODE + "PMC-001", "Unable to create Packet Meta Info from RegistrationDTO"),

	// ZipCreationService
	REG_ADD_ENTRY_TO_ZIP_EXCEPTION(PACKET_CREATION_EXP_CODE + "ZCS-001", "Exception while adding the file to the zip output stream"),
	REG_IO_ZIP_CREATION_EXCEPTION(PACKET_CREATION_EXP_CODE + "ZCS-002", "Exception while closing the zip output stream"),
	REG_ZIP_CREATION_EXCEPTION(PACKET_CREATION_EXP_CODE + "ZCS-003", "Exception while creating packet zip file form RegistrationDTO and provided input files"),

	// PacketEncryptionService
	REG_PACKET_ENCRYPTION_EXCEPTION(PACKET_CREATION_EXP_CODE + "PES-001", "Exception while encrypting the packet zip"),
	REG_PACKET_SIZE_EXCEEDED_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PES-002", "Registration packet size exceeded the configured file size"),

	// AESEncryptionService
	REG_INVALID_DATA_ERROR_CODE(PACKET_CREATION_EXP_CODE + "AES-001", "Invalid Data for Packet Encryption using Symmetric Algorithm"),
	REG_INVALID_KEY_ERROR_CODE(PACKET_CREATION_EXP_CODE + "AES-002", "Invalid Key for Packet Encryption using Symmetric Algorithm"),
	REG_PACKET_AES_ENCRYPTION_EXCEPTION(PACKET_CREATION_EXP_CODE + "AES-003", "Exception while Encrypting the packet zip"),

	// RSAEncryptionService
	REG_INVALID_DATA_RSA_ENCRYPTION(PACKET_CREATION_EXP_CODE + "RSA-001", "Invalid Key or Algorithm for encrypting the data using Assymmetric algorithm"),
	REG_RUNTIME_RSA_ENCRYPTION(PACKET_CREATION_EXP_CODE + "RSA-002", "Exception while Encrypting the packet zip using Assymmetric algorithm"),

	// StorageService
	REG_PACKET_STORAGE_EXCEPTION(PACKET_CREATION_EXP_CODE + "SSI-001", "Exception while storing the packet in configured location"),
	REG_PACKET_STORAGE_RUNTIME_EXCEPTION(PACKET_CREATION_EXP_CODE + "SSI-002", "Runtime Exception while storing the packet in configured location"),

	// RegistrationDAO
	REG_PACKET_SAVE_TO_DB_EXCEPTION(PACKET_CREATION_EXP_CODE + "RDA-001", "Exception while saving the registration details to DB"),

	REG_FILE_NOT_FOUND_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "FUM-001", "File not found for input path"),
	REG_IO_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "RKG-001", "Input-output relation failed"),
	REG_CLASS_NOT_FOUND_ERROR_CODE(PACKET_CREATION_EXP_CODE + "SDU-001", "Class not found for input"),
	REG_FTP_CONNECTION_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "FUM-002","Error in ftp connection"),
	REG_TEMPLATE_IO_EXCEPTION(REG_ACK_EXP_CODE + "TES","Exception while writing the template into file"),
	REG_FTP_PROPERTIES_SET_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "FUM-003","Error in ftp properties"),
	REG_SERVICE_DELEGATE_UTIL_CODE(PACKET_CREATION_EXP_CODE + "SDU-002","Exception through service delegate util, class not found"),
	REG_RSA_INVALID_DATA(PACKET_CREATION_EXP_CODE + "REM-001", "Invalid data for RSA encryption"),
	REG_RSA_INVALID_KEY(PACKET_CREATION_EXP_CODE + "REM-002", "Invalid key for RSA encryption"),
	REG_SERVICE_DUPLICATE_KEY_EXCEPTION_CODE("IDC-FRA-PAC-023","Tried to insert Duplicate key in MAchine Mapping table"),
	REG_UI_SHEDULER_IOEXCEPTION_EXCEPTION("REG-UI-SHE-003", "Unable to load the screen"),
	REG_UI_LOGIN_IO_EXCEPTION("LGN-UI-SHE-004", "IO Exception"),
	REG_UI_LOGIN_RESOURCE_EXCEPTION("LGN-UI-SHE-005", "Unable to load the Resource"),
	REG_UI_LOGOUT_IO_EXCEPTION("REG-UI-SHE-009", "Unable to logout"),
	REG_ACK_TEMPLATE_IO_EXCEPTION("REG-UI-SHE-010","Unable to write the image file"),
	REG_PACKET_SYNC_EXCEPTION("REG-PSS-001","Unable to Sync Packets to the server"),
	REG_PACKET_UPLOAD_ERROR("REG-PUS-001","Unable to Push Packets to the server"),
	REG_ID_JSON_ERROR("REG-JSC-001","Exception while parsing DemographicDTO to ID JSON"),
	REG_ID_JSON_FIELD_ACCESS_ERROR("REG-JSC-002","Exception while accessing fields in DemographicDTO for ID JSON conversion"),
	REG_IRIS_SCANNING_ERROR(RegistrationConstants.USER_REG_IRIS_CAPTURE_EXP_CODE + "IFC-003", "Exception while scanning iris of the individual"),
	REG_FINGERPRINT_SCANNING_ERROR(RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_EXP_CODE+"FCS-002", "Exception while scanning fingerprints of the individual"),
	REG_OTP_VALIDATION("REG-OV-001","Erroe while validating the otp"),
	REG_PACKET_DATE_PARSER_CODE(PACKET_CREATION_EXP_CODE + "TGE-001", "Exception while parsing the date to display in acknowledgement receipt"),
	REG_PACKET_JSON_VALIDATOR_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PCS-003", "Exception while validating ID json file"),
	AUTHZ_ADDING_AUTHZ_HEADER("REG-RCA-001", "Exception while invoking web-service request"),
	INVALID_OTP("REG-SDU-003", "OTP is either invalid or expired"),
	INVALID_RESPONSE_HEADER("REG-SDU-004", "Response header received from the web-service is not as expected"),
	AUTHZ_ADDING_REQUEST_SIGN("REG-RCA-002", "Exception while generating the signature of resquest body"),
	
	// TPM
	TPM_UTIL_SIGN_ERROR("TPM-UTL-001", "Exception while signing the data using TPM"),
	TPM_UTIL_VALIDATE_SIGN_ERROR("TPM-UTL-002",
			"Exception while validating the signature provided by TPM"),
	TPM_UTIL_ASYMMETRIC_ENCRYPT_ERROR("TPM-UTL-003",
			"Exception while encrypting the data using asymmetric crypto-algorithm through TPM"),
	TPM_UTIL_ASYMMETRIC_DECRYPT_ERROR("TPM-UTL-004",
			"Exception while encrypting the data using asymmetric crypto-algorithm through TPM"),
	TPM_UTIL_GET_SIGN_KEY_ERROR("TPM-UTL-005", "Exception while getting the public part of the TPM signing key"),
	TPM_UTIL_GET_TPM_INSTANCE_ERROR("TPM-UTL-006", "Exception while getting the TPM instance"),
	TPM_UTIL_CLOSE_TPM_INSTANCE_ERROR("TPM-UTL-007", "Exception while closing the TPM instance"),
	TPM_INIT_CLOSE_TPM_INSTANCE_ERROR("TPM-INT-001", "Exception while closing the TPM instance"),
	
	// ID Object Validator
	ID_OBJECT_SCHEMA_VALIDATOR("REG-IOS-001", "Invalid ID Object Schema"),
	ID_OBJECT_PATTERN_VALIDATOR("REG-IOS-002", "Invalid ID Object Pattern"),
	ID_OBJECT_MASTER_DATA_VALIDATOR("REG-IOS-003", "Invalid Master Data Object Pattern"),

	// TPM Public Key Upload
	TPM_PUBLIC_KEY_UPLOAD("REG-TPK-001", "Exception while uploading the TPM Public Key"),
	
	//Invalid User
	AUTH_ADVICE_USR_ERROR("REG-SER-ATAD","Provided user id not allowed to do this operation"),

	// Registration Packet Validation
	REG_NULL_PACKET_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-004", "RegistrationDTO can not be null"),
	REG_INVALID_RID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-005", "RID can not be null or empty"),
	REG_PKT_METADATA_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-006", "Registration Meta Data can not be null"),
	REG_PKT_INVALID_CENTER_ID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-007", "Registration Center ID can not be null or empty"),
	REG_PKT_INVALID_MACHINE_ID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-008", "Machine ID can not be null or empty"),
	REG_PKT_INVALID_DEVICE_ID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-09", "Device ID can not be null or empty"),
	REG_PKT_INVALID_REG_CATEGORY_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-010", "Registration Category can not be null or empty"),
	REG_PKT_INVALID_APPLICANT_CONSENT_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-011", "Consent of Applicant can not be null or empty"),
	REG_PKT_BIOMETRICS_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-012", "Biometrics can not be null"),
	REG_PKT_APPLICANT_BIOMETRICS_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-013", "Applicant Biometrics can not be null"),
	REG_PKT_APPLICANT_BIO_INVALID_FACE_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-014", "Applicant Face can not be null"),
	REG_PKT_APPLICANT_BIO_INVALID_EXCEPTION_FACE_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-015", "Applicant Exception Face can not be null"),
	REG_PKT_AUTHENTICATION_BIO_INVALID_EXCEPTION_FACE_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-016", "Applicant Exception Face can not be null"),
	REG_PKT_OSIDATA_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-017", "OSI Data can not be null"),
	REG_PKT_OFFICER_ID_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-018", "Officer ID in OSI Data can not be null or empty"),
	REG_PKT_OFFICER_AUTH_INVALID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-019", "Officer authentication can not be empty"),
	REG_PKT_SUPERVISOR_ID_NULL_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-018", "Supervisor ID in OSI Data can not be null or empty"),
	REG_PKT_SUPERVISOR_AUTH_INVALID_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-019", "Supervisor authentication can not be empty"),
	
	//Notification Service
	NOTIFICATION_MANDATORY_CHECK_SMS_EXCEPTION(PACKET_CREATION_EXP_CODE + "NSI-001", "Message and Number can not be null"),
	NOTIFICATION_MANDATORY_CHECK_EMAIL_EXCEPTION(PACKET_CREATION_EXP_CODE + "NSI-002", "Message and Email can not be null"),
	REG_PKT_FILE_NAME_EXCEPTION(PACKET_CREATION_EXP_CODE + "PUS-002", "File name is missing"),
	REG_PKT_CLIENT_STATUS(PACKET_CREATION_EXP_CODE + "PUS-003", "Packet client status code is missing"),
	REG_PKT_CLIENT_STATUS_COMMENTS(PACKET_CREATION_EXP_CODE + "PUS-004", "Packet client status comments is missing"),
	REG_PKT_SERVER_STATUS(PACKET_CREATION_EXP_CODE + "PUS-005", "Packet server status is missing"),
	REG_PKT_STATUS(PACKET_CREATION_EXP_CODE + "PUS-006", "Packet status value is missing"),
	REG_PKT_HASH(PACKET_CREATION_EXP_CODE + "PUS-007", "Packet hash is missing"),
	REG_PKT_SUPERVISOR_STATUS(PACKET_CREATION_EXP_CODE + "PUS-008", "Supervisor status is missing"),
	REG_PKT_SUPERVISOR_COMMENTS(PACKET_CREATION_EXP_CODE + "PUS-009", "Supervisor comments is missing"),
	REG_PKT_ENCODED_STRING(PACKET_CREATION_EXP_CODE + "PUS-010", "Encoded string can not be empty or null"),
	REG_PKT_TRIGGER_PT(PACKET_CREATION_EXP_CODE + "PUS-010", "Trigger point can not be empty or null"),
	REG_PKT_ID(PACKET_CREATION_EXP_CODE + "PUS-011", "Packet id can not be empty or null"),
	REG_PKT_UPLD_EXCEPTION(PACKET_CREATION_EXP_CODE + "PUS-001", "Upload Packet missing");

	/**
	 * The constructor
	 */
	private RegistrationExceptionConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	private final String errorCode;
	private final String errorMessage;
	
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
