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

	REG_SOCKET_ERROR_CODE(PACKET_CREATION_EXP_CODE +"SMA-001", "No socket is available"),
	REG_NO_SUCH_ALGORITHM_ERROR_CODE(PACKET_CREATION_EXP_CODE + "AKM-001", "No such algorithm available for input"),
	REG_INVALID_DATA_ERROR_CODE(PACKET_CREATION_EXP_CODE + "AEM-001", "Invalid Data for Packet Encryption"),
	REG_INVALID_KEY_ERROR_CODE(PACKET_CREATION_EXP_CODE + "AEM-002", "Invalid key for input"),
	REG_INVALID_KEY_SEED_ERROR_CODE("REG-PRO-PAC-009", "Invalid seeds for key generation"),
	REG_IO_EXCEPTION(PACKET_CREATION_EXP_CODE + "ZCM-001", "IO exception"),
	REG_JSON_PROCESSING_EXCEPTION(PACKET_CREATION_EXP_CODE + "PCS-001", "Exception while parsing object to JSON"),
	REG_FILE_NOT_FOUND_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "FUM-001", "File not found for input path"),
	REG_IO_ERROR_CODE(PACKET_UPLOAD_EXP_CODE + "RKG-001", "Input-output relation failed"),
	REG_CLASS_NOT_FOUND_ERROR_CODE(PACKET_CREATION_EXP_CODE + "SDU-001", "Class not found for input"),
	REG_PACKET_CREATION_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PHS-001", "Exception while creating Registration"),
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
	REG_PACKET_SIZE_EXCEEDED_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PES-001", "Registration packet size exceeded the configured file size"),
	REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PCS-002", "Exception while creating CBEFF file for biometrics"),
	REG_PACKET_JSON_VALIDATOR_ERROR_CODE(PACKET_CREATION_EXP_CODE + "PCS-003", "Exception while validating ID json file"),
	AUTHZ_ADDING_AUTHZ_HEADER("REG-RCA-001", "Exception while adding authorization token to web-service request"),
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
	TPM_INIT_CLOSE_TPM_INSTANCE_ERROR("TPM-INT-001", "Exception while closing the TPM instance");

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
