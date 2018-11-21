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
	PHONE_EMAIL_NOT_REGISTERED("IDA-OTA-001",
			"Phone no and e-mail not registered. Individual has to register and try again","A0105"),
	INVALID_OTP_REQUEST_TIMESTAMP("IDA-OTA-002", "Request received at MOSIP is %s min post the initiation at TSP","A0106"),
	OTP_REQUEST_FLOODED("IDA-OTA-003", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-004", "Could not generate/send OTP"),
	EXPIRED_OTP("IDA-OTA-005", "OTP has expired","A0107"),
	INVALID_OTP("IDA-OTA-006", "OTP is invalid","A0108"),
	INVALID_TXN_ID("IDA-OTA-007", "Input txnID does not match txnID of OTP Request","A0109"),
	OTP_NOT_PRESENT("IDA-OTA-008", "Missing OTP value","A0110"),
	REQUEST_PROCESSING_ERROR("IDA-OTA-010", "Request could not be processed. Please try again"),

	// To be discussed with BA
	INVALID_AUTH_REQUEST_TIMESTAMP("IDA-MLC-001", "Request to be received at MOSIP within %s hrs/min"),
	INVALID_UIN("IDA-MLC-002", "Invalid UIN","A0101"),
	UIN_DEACTIVATED("IDA-MLC-003", "UIN has been deactivated","A0102"),
	INVALID_VID("IDA-MLC-004", "Invalid VID", "A0103"), 
	EXPIRED_VID("IDA-MLC-005", "Expired VID", "A0104"), // Not
																											// referenced
	INACTIVE_VID("IDA-IDV-005", "Inactive VID"), AUTHENTICATION_FAILED("IDA-AUT-501", "Authentication failed"),
	DATA_VALIDATION_FAILED("IDA-IDV-001", "Input Data Validation Failed"),
	INVALID_INPUT_PARAMETER("IDA-IDV-002", "Invalid Input Parameter - %s"),
	MISSING_INPUT_PARAMETER("IDA-IDV-003", "Missing Input Parameter - %s"),


	// eKYC validation messages

	INVALID_EKYC_CONCENT("IDA-DEA-030", "Invalid resident eKYC consent"),
	INVALID_EKYC_AUTHTYPE("IDA-DEA-035",
			"Invalid resident auth type (\"kycAuthType \" does not match with data in PID block)"),
	UNAUTHORISED_KUA("IDA-DEA-032", "Unauthorised KUA"),

	// Internal Errors
	ID_MAPPING_FAILED("IDA-IDV-101", "VID is not present or not matched"),
	INVALID_IDTYPE("IDA-IDV-102", "Invalid Id-Type"),
	INVALID_OTP_KEY("IDA-OTA-101", "Key is Null or Invalid"),
	KERNEL_OTP_VALIDATION_REQUEST_FAILED("IDA-OTA-102", "Kernel Validate OTP request failed"),
	KERNEL_OTP_GENERATION_REQUEST_FAILED("IDA-OTA-103", "Kernel Generate OTP request failed"),

	NOTIFICATION_FAILED("IDA-NTS-101", "Notification Failed"),

	MISSING_TEMPLATE_CONFIG("IDA-ITM-102", "Template Configuration missing"),
	PDF_NOT_GENERATED("IDA-ITM-102", "Unable to Generate PDF"),

	// Rest Helper Error Constants
	INVALID_URI("IDA-RST-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-005", "Timeout is invalid"), 
	CLIENT_ERROR("IDA-RST-006", "4XX - Client Error occured"),
	SERVER_ERROR("IDA-RST-007", "5XX - Server Error occured"),
	CONNECTION_TIMED_OUT("IDA-RST-008", "Connection timed out"),

	INVALID_AUTH_REQUEST("IDA-RQV-101", "Invalid Auth Request"), 
	UNKNOWN_ERROR("IDA-MLC-101", "Unknown error occured"),

	// Demo Validation
	INVALID_FULL_ADDRESS_REQUEST("IDA-DEA-015", "Required Full Address(fad) attribute is missing"),
	INVALID_ADDRESS_REQUEST("IDA-DEA-014", "Required Address(ad) attribute is missing"),
	DOB_TYPE_MISMATCH("IDA-DEA-033", "Demographic data – DOB Type (pi) did not match", "A0142"),
	AD_FAD_MUTUALLY_EXCULUSIVE("IDA-AD-RQV-003", "Full Address and Address are mutually exclusive"),
	INVALID_PERSONAL_INFORMATION("IDA-PRSNL-RQV-001", "Atleat one valid attribute should be present");

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
