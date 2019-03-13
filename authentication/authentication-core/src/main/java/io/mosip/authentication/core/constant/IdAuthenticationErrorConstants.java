/**
 * 
 */
package io.mosip.authentication.core.constant;

/**
 * List of all IDA error codes and its respective error messages.
 * 
 * @author Manoj SP
 *
 */
public enum IdAuthenticationErrorConstants {

	INVALID_UIN("IDA-MLC-002", "Invalid UIN", "A0101"),
	UIN_DEACTIVATED("IDA-MLC-003", "UIN has been deactivated", "A0102"),
	INVALID_VID("IDA-MLC-004", "Invalid VID", "A0103"), 
	EXPIRED_VID("IDA-MLC-005", "Expired VID", "A0104"),
	
	// OTP
	PHONE_EMAIL_NOT_REGISTERED("IDA-OTA-001",
			"Phone no or e-mail not registered. Individual has to register and try again", "A0105"),
	INVALID_OTP_REQUEST_TIMESTAMP("IDA-OTA-002", "Request to be received at MOSIP within 20 min", "A0106"),
	OTP_REQUEST_FLOODED("IDA-OTA-003", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-004", "Could not generate/send OTP"),
	EXPIRED_OTP("IDA-OTA-005", "OTP has expired", "A0107"),
	INVALID_OTP("IDA-OTA-006", "OTP is invalid", "A0108"),
	INVALID_TXN_ID("IDA-OTA-007", "Input transactionID does not match transactionID of OTP Request"),
	UIN_LOCKED("IDA-OTA-008", "UIN is locked for OTP generation. Please try again later"),
	BLOCKED_OTP_TO_GENERATE("IDA-OTA-009", "UIN is locked for OTP validation due to exceeding no of invalid OTP trials"),

	MISSING_INPUT_PARAMETER("IDA-MLC-006", "Missing Input Parameter - %s"),
	UNABLE_TO_PROCESS("IDA-MLC-007", "Request could not be processed. Please try again"),
	DEMOGRAPHIC_DATA_MISMATCH("IDA-DEA-001", "Demographic data  %s  in  %s did not match", "A0109"),
	INVALID_TIMESTAMP("IDA-MLC-001", "Please send the request within %s hrs/min", "A0110"),
	UNSUPPORTED_LANGUAGE("IDA-DEA-002", "Unsupported Language Code - %s", "A0111"),
	DEMO_DATA_MISMATCH("IDA-DEA-003", "Demographic data - %s did not match", "A0112"),
	MISSING_AUTHTYPE("IDA-MLC-015", "Missing  %s auth attribute"),
	BIO_MISMATCH("IDA-BIA-001", "Biometric data - %s did not match", "A0113"),
	DUPLICATE_FINGER("IDA-BIA-003", "Duplicate fingers in request", "A0114"),
	FMR_INVALID("IDA-BIA-005", "Single FMR record contains more than one finger", "A0115"),
	FINGER_EXCEEDING("IDA-BIA-006", "Number of FMR should not exceed 2"), 
	INVALID_DCODE("IDA-BIA-007", "Invalid dCode"),
	INVALID_MID("IDA-BIA-008", "Invalid mId"), 
	INVALID_BIOMETRIC("IDA-BIA-009", "Invalid biometric data"),
	MISSING_BIOMETRICDATA("IDA-BIA-010", "Missing biometric data"),
	BIOMETRIC_MISSING("IDA-BIA-011", "Missing biometric data in MOSIP database for the given UIN/Virtual ID", "A0116"),
	BIOTYPE_MISSING("IDA-BIA-012", "Missing or empty value for bioType"),
	INVALID_BIOTYPE("IDA-BIA-013", "Invalid bioType"),
	IRISIMG_MISMATCH("IDA-BIA-015", "Biometric data - IrisImg did not match", "A0117"),
	DUPLICATE_IRIS("IDA-BIA-016", "Duplicate Irises in request", "A0118"),
	IRIS_EXCEEDING("IDA-BIA-017", "Number of IIR should not exceed 2"),
	INDIVIDUAL_DATA_MISSING("IDA-EKA-001", "Individuals data currently not available"),
	UNAUTHORISED_PARTNER("IDA-MPA-013", "Partner is unauthorised for eKYC"),
	DSIGN_FALIED("IDA-MPA-001", "Digital signature verification failed"),
	INVALID_CERTIFICATE("IDA-MPA-002", "Invalid certificate used in digital signature"),
	INVALID_ENCRYPTION("IDA-MPA-003", "Unable to decrypt Authentication Request"),
	INVALID_INPUT_PARAMETER("IDA-MLC-009", "Invalid Input Parameter - %s"),
	FID_INVALID("IDA-BIA-018", "Biometric data - FID did not match", "A0119"),
	FID_EXCEEDS("IDA-BIA-019", "Number of FID records should not exceed 1"),
	PIN_MISMATCH("IDA-SPA-001", "Pin value did not match", "A0120"),
	NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST("IDA-MLC-008", "No authentication type selected"),
	VID_GENERATION_FAILED("IDA-MLC-010", "Could not generate VID"),
	VID_REGENERATION_FAILED("IDA-MLC-011", "VID regeneration not allowed. Use existing VID"),
	VID_DEACTIVATED_UIN("IDA-MLC-012", "VID corresponding to a deactivated UIN"),
	AUTH_TYPE_NOT_SUPPORTED("IDA-MLC-013", "Unsupported Authentication Type - %s", "A0121"),
	PUBLICKEY_EXPIRED("IDA-MPA-004", "MOSIP Public key expired"),
	OTPREQUEST_NOT_ALLOWED("IDA-MPA-005", "OTP Request Usage not allowed as per policy"),
	AUTHTYPE_NOT_ALLOWED("IDA-MPA-006", "Authentiation Usage not allowed as per policy"),
	INVALID_LICENSEKEY("IDA-MPA-007", "License key does not belong to a registered MISP"),
	LICENSEKEY_EXPIRED("IDA-MPA-008", "License key of MISP has expired"),
	PARTNER_NOT_REGISTERED("IDA-MPA-009", "Partner is not registered"),
	PARTNER_NOT_MAPPED("IDA-MPA-010", "MISP and Partner not mapped"),
	LICENSEKEY_SUSPENDED("IDA-MPA-011", "License key of MISP is suspended"),
	PARTNER_DEACTIVATED("IDA-MPA-012", "Partner is deactivated"),
	CONSENT_NOT_AVAILABLE("IDA-MLC-014", "Individual's Consent is not available"),
	PIN_NOT_STORED("IDA-SPA-002", "Could not store the static pin of the individual"),
	DATA_VALIDATION_FAILED("IDA-IDV-001", "Input Data Validation Failed"),

	// Rest Helper Error Constants
	INVALID_URI("IDA-RST-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-005", "Timeout is invalid"), CLIENT_ERROR("IDA-RST-006", "4XX - Client Error occured"),
	SERVER_ERROR("IDA-RST-007", "5XX - Server Error occured"),
	CONNECTION_TIMED_OUT("IDA-RST-008", "Connection timed out")

	;

	private final String errorCode;
	private final String errorMessage;
	private String actionCode;

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

	private IdAuthenticationErrorConstants(String errorCode, String errorMessage, String actionCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.actionCode = actionCode;
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

	public String getActionCode() {
		return actionCode;
	}
}
