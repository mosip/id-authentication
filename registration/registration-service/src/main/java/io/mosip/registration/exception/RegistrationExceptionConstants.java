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
	REG_USER_MACHINE_MAP_MACHINE_MASTER_CODE("IDC-FRA-UMM-024","No Record Found in the Machine Master table"),
	REG_USER_MACHINE_MAP_CENTER_MACHINE_CODE("IDC-FRA-UMM-025","No Record Found in the Center Machine table"),
	REG_USER_MACHINE_MAP_CENTER_USER_MACHINE_CODE("IDC-FRA-UMM-025","No Record Found in the Center USER Machine table"),	
	REG_UI_SHEDULER_ARG_EXCEPTION("REG-UI-SHE-001", "Please verify the argument passed"),
	REG_UI_SHEDULER_STATE_EXCEPTION("REG-UI-SHE-002", "The state not found"),
	REG_UI_SHEDULER_IOEXCEPTION_EXCEPTION("REG-UI-SHE-003", "Unable to load the screen"),
	REG_UI_LOGIN_IO_EXCEPTION("LGN-UI-SHE-004", "IO Exception"),
	REG_UI_LOGIN_RESOURCE_EXCEPTION("LGN-UI-SHE-005", "Unable to load the Resource"),
	REG_UI_LOGOUT_IO_EXCEPTION("REG-UI-SHE-009", "Unable to logout"),
	REG_ACK_TEMPLATE_IO_EXCEPTION("REG-UI-SHE-010","Unable to write the image file"),
	REG_PACKET_SYNC_EXCEPTION("REG-PSS-001","Unable to Sync Packets to the server"),
	REG_PACKET_UPLOAD_ERROR("REG-PUS-001","Unable to Push Packets to the server"),
	REG_ID_JSON_ERROR("REG-JSC-001","Exception while parsing DemographicDTO to ID JSON"),
	REG_ID_JSON_FIELD_ACCESS_ERROR("REG-JSC-002","Exception while accessing fields in DemographicDTO for ID JSON conversion"),
	REG_IRIS_SCANNING_ERROR(RegistrationConstants.USER_REG_IRIS_CAPTURE_EXP_CODE + "ICC-001", "Exception while scanning iris of the individual"),
	REG_FINGERPRINT_SCANNING_ERROR(RegistrationConstants.USER_REG_FINGERPRINT_CAPTURE_EXP_CODE+"FCS-002", "Exception while scanning fingerprints of the individual");

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
