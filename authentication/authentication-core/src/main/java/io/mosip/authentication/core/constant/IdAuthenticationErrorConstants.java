/**
 * 
 */
package io.mosip.authentication.core.constant;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * List of all IDA error codes and its respective error messages.
 * 
 * @author Manoj SP
 *
 */
public enum IdAuthenticationErrorConstants {

	// OTP
	OTP_REQUEST_FLOODED("IDA-OTA-001", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-002", "Could not generate/send OTP"),
	EXPIRED_OTP("IDA-OTA-003", "OTP has expired", "Please regenerate OTP and try again after sometime"),
	INVALID_OTP("IDA-OTA-004", "OTP is invalid", "Please provide correct OTP value"),
	INVALID_TXN_ID("IDA-OTA-005", "Input transactionID does not match transactionID of OTP Request"),
	BLOCKED_OTP_GENERATE("IDA-OTA-006", "UIN is locked for OTP generation. Please try again later"),
	BLOCKED_OTP_VALIDATE("IDA-OTA-007", "UIN is locked for OTP validation due to exceeding no of invalid OTP trials"),
	OTP_CHANNEL_NOT_PROVIDED("IDA-OTA-008", "OTP Notification Channel not provided"),
	OTP_CHANNEL_NOT_CONFIGURED("IDA-OTA-009", "%s not configured for the country"),
	OTP_ID_MISMATCH("IDA-OTA-010", "Input transactionID does not match transactionID of OTP Request"),
	

	INVALID_TIMESTAMP("IDA-MLC-001", "Request to be received at MOSIP within %s minutes",
			"Please send the request within %s minutes"),
	INVALID_UIN("IDA-MLC-002", "Invalid UIN", "Please retry with the correct UIN"),
	UIN_DEACTIVATED("IDA-MLC-003", "UIN has been deactivated", "Your UIN status is not active"),
	INVALID_VID("IDA-MLC-004", "Invalid VID", "Please retry with correct VID"),
	EXPIRED_VID("IDA-MLC-005", "%s", "Please regenerate VID and try again"),
	MISSING_INPUT_PARAMETER("IDA-MLC-006", "Missing Input Parameter - %s"),
	UNABLE_TO_PROCESS("IDA-MLC-007", "Request could not be processed. Please try again"),
	NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST("IDA-MLC-008", "No authentication type selected"),
	INVALID_INPUT_PARAMETER("IDA-MLC-009", "Invalid Input Parameter - %s"),
	VID_DEACTIVATED_UIN("IDA-MLC-010", "VID corresponding to a deactivated UIN"),
	AUTH_TYPE_NOT_SUPPORTED("IDA-MLC-011", "Unsupported Authentication Type - %s",
			"Please use other Authentication Types in the request"),
	CONSENT_NOT_AVAILABLE("IDA-MLC-012", "Individual's Consent is not available"),
	MISSING_AUTHTYPE("IDA-MLC-013", "Missing %s auth attribute"),
	PHONE_EMAIL_NOT_REGISTERED("IDA-MLC-014", "%s not registered. Individual has to register and try again",
			"Please register your %s and try again"),
	IDENTITYTYPE_NOT_ALLOWED("IDA-MLC-015", "Identity Type - %s not configured for the country"),
	INVALID_TXNID_BIO("IDA-MLC-016", "Transaction ID parameters in the request does not match"),
	INVALID_USERID( "IDA-MLC-017","Invalid UserID"),
	ID_NOT_AVAILABLE("IDA-MLC-018", "%s not available in database"),
	
	
	  DEMOGRAPHIC_DATA_MISMATCH_LANG("IDA-DEA-001", "Demographic data %s in %s did not match",
				"Please re-enter your %s in %s"),
		DEMO_DATA_MISMATCH("IDA-DEA-001", "Demographic data %s did not match", "Please re-enter your %s"),
	UNSUPPORTED_LANGUAGE("IDA-DEA-002", "Unsupported Language Code - %s", "Please provide valid Language"),
    DEMO_MISSING("IDA-DEA-003", "Demographic data %s not available in database"),
	DEMO_MISSING_LANG("IDA-DEA-003", "Demographic data %s in %s not available in database"),

	BIO_MISMATCH("IDA-BIA-001", "Biometric data – %s did not match", "Please give your biometrics again"),
	DUPLICATE_FINGER("IDA-BIA-002", "Duplicate fingers in request", "Please try again with distinct fingers"),
	FINGER_EXCEEDING("IDA-BIA-003", "Number of FMR should not exceed 2"),
	INVALID_DEVICEID("IDA-BIA-004", "Device not registered with MOSIP"),
	INVALID_PROVIDERID("IDA-BIA-005", "Device provider not registered with MOSIP"),
	BIOMETRIC_MISSING("IDA-BIA-006", "Biometric data %s not available in database",
			"Your Biometric data is not available in MOSIP"),
	DUPLICATE_IRIS("IDA-BIA-007", "Duplicate Irises in request", "Please try again with distinct Irises"),
	IRIS_EXCEEDING("IDA-BIA-008", "Number of IIR should not exceed 2"),
	FACE_EXCEEDING("IDA-BIA-009", "Number of FID records should not exceed 1"),
	FACE_EXCEEDING_FMR("IDA-BIA-010", "Single FMR record contains more than one finger"),
	INVALID_BIOMETRIC("IDA-BIA-011", "Invalid biometric data"),

	

	INVALID_ENCRYPT_EKYC_RESPONSE("IDA-EKA-001", "Unable to encrypt eKYC response"),
	INVALID_REQ_PRINT_FORMAT("IDA-EKA-002", "Invalid value in print format request"),
	

	PIN_MISMATCH("IDA-SPA-001", "Pin value did not match", "Please re-enter your pin"),
	PIN_NOT_STORED("IDA-SPA-002", "Could not store the static pin of the individual"),

	DSIGN_FALIED("IDA-MPA-001", "Digital signature verification failed"),
	INVALID_CERTIFICATE("IDA-MPA-002", "Invalid certificate in digital signature"),
	INVALID_ENCRYPTION("IDA-MPA-003", "Unable to decrypt Request"),
	PUBLICKEY_EXPIRED("IDA-MPA-004", "MOSIP Public key expired"),
	OTPREQUEST_NOT_ALLOWED("IDA-MPA-005", "OTP Request Usage not allowed as per policy"),
	AUTHTYPE_NOT_ALLOWED("IDA-MPA-006", "%s-authentiation usage not allowed as per policy",
			"Please use other Authentication Types in the request"),
	INVALID_LICENSEKEY("IDA-MPA-007", "License key does not belong to a registered MISP"),
	LICENSEKEY_EXPIRED("IDA-MPA-008", "License key of MISP has expired"),
	PARTNER_NOT_REGISTERED("IDA-MPA-009", "Partner is not registered"),
	PARTNER_NOT_MAPPED("IDA-MPA-010", "MISP and Partner not mapped"),
	LICENSEKEY_SUSPENDED("IDA-MPA-017", "License key of MISP is blocked"),
	PARTNER_DEACTIVATED("IDA-MPA-012", "Partner is deactivated"),
	UNAUTHORISED_PARTNER("IDA-MPA-013", "Partner is unauthorised for eKYC"),
	PARTNER_POLICY_NOTMAPPED("IDA-MPA-014", "Partner is not assigned with any policy"),
	AUTHTYPE_MANDATORY("IDA-MPA-015", "%s-authentiation usage is mandatory as per policy"),

	DATA_VALIDATION_FAILED("IDA-IDV-001", "Input Data Validation Failed"),

	// Rest Helper Error Constants
	INVALID_URI("IDA-RST-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-005", "Timeout is invalid"), CLIENT_ERROR("IDA-RST-006", "4XX - Client Error occurred"),
	SERVER_ERROR("IDA-RST-007", "5XX - Server Error occurred"),
	CONNECTION_TIMED_OUT("IDA-RST-008", "Connection timed out"),
	
	HMAC_VALIDATION_FAILED("IDA-MPA-016", "HMAC Validation failed")

	;

	private final String errorCode;
	private final String errorMessage;
	private String actionMessage;

	/**
	 * Constructor for {@link IdAuthenticationErrorConstants}
	 * 
	 * @param errorCode    - id-usage error codes which follows
	 *                     "<product>-<module>-<component>-<number>" pattern
	 * @param errorMessage - short error message
	 */
	private IdAuthenticationErrorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	private IdAuthenticationErrorConstants(String errorCode, String errorMessage, String actionMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.actionMessage = actionMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getActionMessage() {
		return actionMessage;
	}

	public static Optional<String> getActionMessageForErrorCode(String errorCode) {
		return Stream.of(values()).filter(ele -> ele.getErrorCode().equals(errorCode))
				.map(ele -> ele.getActionMessage()).filter(act -> act != null).findAny();
	}

}
