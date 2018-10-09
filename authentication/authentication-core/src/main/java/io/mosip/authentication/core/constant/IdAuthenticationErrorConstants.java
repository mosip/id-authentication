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
			"Phone no and e-mail not registered. Individual has to register and try again"),
	INVALID_OTP_REQUEST_TIMESTAMP("IDA-OTA-002", "Request received at MOSIP is 20 min post the initiation at TSP"),
	OTP_REQUEST_FLOODED("IDA-OTA-003", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-004", "Could not generate/send OTP"), EXPIRED_OTP("IDA-OTA-005", "OTP has expired"),
	INVALID_OTP("IDA-OTA-006", "OTP is invalid"),
	INVALID_TXN_ID("IDA-OTA-007", "Input txnID does not match txnID of OTP Request"),
	INVALID_AUTH_REQUEST_TIMESTAMP("IDA-OTA-008", "Request received at MOSIP <x> hrs/min post the initiation at TSP"),
	OTP_NOT_PRESENT("IDA-OTA-009", "Missing OTP value"),
	REQUEST_PROCESSING_ERROR("IDA-OTA-010", "Request could not be processed. Please try again"),

	// To be discussed with BA
	INVALID_UIN("IDA-MLC-002", "Invalid UIN"), INACTIVE_UIN("IDA-IDV-002", "Inactive UIN"),
	INVALID_VID("IDA-MLC-004", "Invalid VID"), EXPIRED_VID("IDA-IDV-004", "Expired VID"), // Not referenced
	INACTIVE_VID("IDA-IDV-005", "Inactive VID"), AUTHENTICATION_FAILED("IDA-AUT-501", "Authentication failed"),
	DATA_VALIDATION_FAILED("IDA-IDV-001", "Input Data Validation Failed"),

	// ID_VERIFICATION_FAILED_EXCEPTION("IDA-DTV-IDV-001", "Invalid UIN"),
	// Internal Errors
	ID_MAPPING_FAILED("IDA-IDV-101", "VID is not present or not matched"),
	INVALID_IDTYPE("IDA-IDV-102", "Invalid Id-Type"), INVALID_OTP_KEY("IDA-OTA-101", "Key is Null or Invalid"),
	KERNEL_OTP_VALIDATION_REQUEST_FAILED("IDA-OTA-102", "Kernel Validate OTP request failed"),
	KERNEL_OTP_GENERATION_REQUEST_FAILED("IDA-OTA-103", "Kernel Generate OTP request failed"),

	// Rest Helper Error Constants
	INVALID_URI("IDA-RST-DTV-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-DTV-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-DTV-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-DTV-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-DTV-005", "Timeout is invalid"),
	CLIENT_ERROR("IDA-RST-UTL-001", "4XX - Client Error occured"),
	SERVER_ERROR("IDA-RST-UTL-002", "5XX - Server Error occured"),
	CONNECTION_TIMED_OUT("IDA-RST-UTL-003", "Connection timed out"),

	// INVALID_GENERATE_OTP_REQUEST("IDA-DTV-RQV-001", "Invalid OTP request"),
	// OTP_GENERATION_REQUEST_FAILED("IDA-DTV-RQV-002", "OTP generation request
	// failed"),
	// OTP_REQUEST_FLOODED("IDA-DTV-RQV-006","OTP Generation request is Flooded"),

	// OTP_KEY_INVALID("IDA-DTV-IDV-006", "OTP Key is Null or Invalid"),

	// EXPIRED_OTP_REQUEST_TIME("IDA-DTV-RQV-005","OTP requested time expired"),

	// REFID_INVALID("IDA-DTV-IDV-008", "Reference ID is Null or Invalid"),
	// UIN_INVALID("IDA-DTV-IDV-009", "UIN is Null or Invalid"),

	// TXNID_INVALID("IDA-DTV-IDV-010", "Transaction ID is Null or Invalid"),
	// TEMPID_INVALID("IDA-DTV-RQV-003", "Temporavary ID is Null or Invalid"),
	OTP_VALDIATION_REQUEST_FAILED("IDA-DTV-IDV-005", "OTP Validation Request Failed"),

	ID_INVALID_VALIDATEOTP_REQUEST("IDA-DTV-ARV-001", "Invalid Validate OTP request"),

	INVALID_AUTH_REQUEST("IDA-DTV-RQV-101", "Invalid Auth Request"),
	UNKNOWN_ERROR("IDA-DTV-IDV-004", "Unknown error occured"),

	// INVALID_UIN_BUISNESS("IDA-IDMB-UIN-401", "Invalid UIN"),
	// INVALID_VID_BUISNESS("IDA-IDMB-VID-402", "Invalid VID"),

	// INVALID_UIN_VID("IDA-IDMB-IDT-602", "Invalid UIN/VID"),
	INCORRECT_IDTYPE("IDA-DTV-IDT-601", "INCORRECT IDTYPE"), EMPTY_OTP("IDA-DTV-IDT-601", "OTP value is empty"),
	SERVICE_NOT_AVAILABLE("IDA-RST-SRV-001", "Service is not available"),

	// =====================================================================
	// ||************************** Demo Validation **********************||
	// =====================================================================
	INVALID_FULL_ADDRESS_REQUEST("IDA-FAD-RQV-001", "Please select one of language for full address"),
	INVALID_FULL_ADDRESS_REQUEST_PRI("IDA-FAD-RQV-002", "At least one attribute of full address should be present for primary language"),
	INVALID_FULL_ADDRESS_REQUEST_SEC("IDA-FAD-RQV-003", "At least one attribute of full address should be present for secondary language"),
	INVALID_ADDRESS_REQUEST("IDA-AD-RQV-001", "Please select one of language for address"),
	INVALID_ADDRESS_REQUEST_PRI("IDA-AD-RQV-002", "At least one attribute of address should be present for primary language"),
	INVALID_ADDRESS_REQUEST_SEC("IDA-AD-RQV-003", "At least one attribute of address should be present for secondary language"),
	AD_FAD_MUTUALLY_EXCULUSIVE("IDA-AD-RQV-003", "Full Address and Address are mutually exclusive"),
	INVALID_PERSONAL_INFORMATION("IDA-PRSNL-RQV-001", "Atleat one valid attribute should be present"),
	INVALID_PERSONAL_INFORMATION_PRI("IDA-PRSNL-RQV-001","Primary Language code (langPri) should be present"),
	INVALID_PERSONAL_INFORMATION_SEC("IDA-PRSNL-RQV-002","Primary Language code (langSec) should be present"),
	INVALID_DOB_YEAR("IDA-DOB-RQV-001","DOB year should not excedded from current year");
	
	
	private final String errorCode;
	private final String errorMessage;

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
}
