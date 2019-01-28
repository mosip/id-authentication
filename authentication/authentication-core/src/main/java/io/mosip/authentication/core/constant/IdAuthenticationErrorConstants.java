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
			"Phone no and e-mail not registered. Individual has to register and try again", "A0105"),
	INVALID_OTP_REQUEST_TIMESTAMP("IDA-OTA-002", "Request received at MOSIP is %s min post the initiation at TSP",
			"A0106"),
	OTP_REQUEST_FLOODED("IDA-OTA-003", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-004", "Could not generate/send OTP"),
	EXPIRED_OTP("IDA-OTA-005", "OTP has expired", "A0107"), 
	INVALID_OTP("IDA-OTA-006", "OTP is invalid", "A0108"),
	INVALID_TXN_ID("IDA-OTA-007", "Input txnID does not match txnID of OTP Request", "A0109"),
	OTP_NOT_PRESENT("IDA-OTA-008", "Missing OTP value", "A0110"),
	//REQUEST_PROCESSING_ERROR("IDA-OTA-010", "Request could not be processed. Please try again"),
	BLOCKED_OTP_TO_GENERATE("IDA-OTA-010", "Could not generate OTP as UIN is locked "),
	BLOCKED_OTP_TO_VALIDATE("IDA-OTA-011", "UIN locked due to exceeding no of invalid OTP trials"),

	// To be discussed with BA
	INVALID_AUTH_REQUEST_TIMESTAMP("IDA-MLC-001", "Request to be received at MOSIP within %s hrs/min"),
	INVALID_UIN("IDA-MLC-002", "Invalid UIN", "A0101"),
	UIN_DEACTIVATED("IDA-MLC-003", "UIN has been deactivated", "A0102"),
	INVALID_VID("IDA-MLC-004", "Invalid VID", "A0103"), 
	EXPIRED_VID("IDA-MLC-005", "Expired VID", "A0104"), // Not
																											// referenced
	INACTIVE_VID("IDA-IDV-005", "Inactive VID"), 
	AUTHENTICATION_FAILED("IDA-AUT-501", "Authentication failed"),
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
	INVALID_IDTYPE("IDA-IDV-102", "Invalid Id-Type"), INVALID_OTP_KEY("IDA-OTA-101", "Key is Null or Invalid"),
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
	INVALID_TIMEOUT("IDA-RST-005", "Timeout is invalid"), CLIENT_ERROR("IDA-RST-006", "4XX - Client Error occured"),
	SERVER_ERROR("IDA-RST-007", "5XX - Server Error occured"),
	CONNECTION_TIMED_OUT("IDA-RST-008", "Connection timed out"),

	INVALID_AUTH_REQUEST("IDA-RQV-101", "Invalid Auth Request"), UNKNOWN_ERROR("IDA-MLC-101", "Unknown error occured"),

	// Demo Validation
	NAMEPRI_MISMATCH("IDA-DEA-001", "Demographic data - Name in primary language(pi) did not match", "A0111"),
	NAMESEC_MISMATCH("IDA-DEA-002", "Demographic data - Name in secondary language(pi) did not match", "A0112"),
	FAD_PRI_MISMATCH("IDA-DEA-011", "Demographic data - Address in primary language(fad) did not match", "A0123"),
	FAD_SEC_MISMATCH("IDA-DEA-012", "Demographic data - Address in secondary language(fad) did not match", "A0124"),
	ADDR_PRI_MISMATCH("IDA-DEA-013", "Demographic data - Address Line Items in primary language (ad) did not match",
			"A0126"),
	ADDR_SEC_MISMATCH("IDA-DEA-028", "Demographic data - Address Line Items in secondary language (ad) did not match",
			"A0127"),
	MISSING_PI("IDA-DEA-003", "Required Identity(pi) attribute is missing", "A0114"),
	MISSING_AD("IDA-DEA-014", "Required Address(ad) attribute is missing", "A0128"),
	MISSING_FAD("IDA-DEA-015", "Required Full Address(fad) attribute is missing", "A0129"),
	INVALID_MATCHINGTHRESHOLD_PI_PRI("IDA-DEA-007", "Invalid mtPri (Pi)", "A0118"),
	INVALID_MATCHINGTHRESHOLD_PI_SEC("IDA-DEA-008", "Invalid mtSec (Pi)", "A0119"),
	INVALID_MATCHINGSTRATEGY_PI_PRI("IDA-DEA-009", "Invalid msPri (Pi)", "A0120"),
	INVALID_MATCHINGSTRATEGY_PI_SEC("IDA-DEA-010", "Invalid msSec (Pi)", "A0121"),
	INVALID_MATCHINGTHRESHOLD_FAD_PRI("IDA-DEA-016", "Invalid mtPri (fad)", "A0130"),
	INVALID_MATCHINGTHRESHOLD_FAD_SEC("IDA-DEA-017", "Invalid mtSec (fad)", "A0131"),
	INVALID_MATCHINGSTRATEGY_FAD_PRI("IDA-DEA-018", "Invalid msPri (fad)", "A0132"),
	INVALID_MATCHINGSTRATEGY_FAD_SEC("IDA-DEA-019", "Invalid msSec (fad)", "A0133"),
	INVALID_FULL_ADDRESS_REQUEST("IDA-DEA-015", "Required Full Address(fad) attribute is missing"),
	INVALID_ADDRESS_REQUEST("IDA-DEA-014", "Required Address(ad) attribute is missing"),
	GENDER_MISMATCH("IDA-DEA-025", "Demographic data - Gender(pi) did not match", "A0139"),
	EMAIL_MISMATCH("IDA-DEA-027", "Demographic data - email(pi) did not match", "A0141"),
	DOB_TYPE_MISMATCH("IDA-DEA-033", "Demographic data - DOB Type (pi) did not match", "A0142"),
	AD_FAD_MUTUALLY_EXCULUSIVE("IDA-AD-RQV-003", "Full Address and Address are mutually exclusive"),
	INVALID_PERSONAL_INFORMATION("IDA-PRSNL-RQV-001", "Atleat one valid attribute should be present"),
	PHONE_MISMATCH("IDA-DEA-026", "Demographic data - Phone(pi) did not match", "A0140"),
	AGE_MISMATCH("IDA-DEA-028", "Demographic data - Age(pi) did not match", "A0141"),
	DOB_MISMATCH("IDA-DEA-023", "Demographic data - DOB(pi) did not match", "A0137"),
	
	//pin validation
	PIN_MISMATCH("IDA-SPA-001", "Pin value did not match", "A0151"),
	MISSING_PINDATA("IDA-SPA-002","Missing pinval in the request"),
	// Bio validation
	DUPLICATE_FINGER("IDA-BIA-003", "Duplicate fingers in request.", "A0145"),
	DUPLICATE_IRIS("IDA-BIA-016", "Duplicate Irises in request", "A0149"),
	INVALID_BIOTYPE("IDA-BIA-013","Invalid bioType - %s"),
	FINGER_EXCEEDING("IDA-BIA-006", "Number of fgerMin / fgerImg should not exceed 10."),
	IRIS_EXCEEDING("IDA-BIA-017", "Number of irisImg should not exceed 2"),
	MISSING_BIOMETRICDATA("IDA-BIA-010", "Missing biometric data"),
	FGRMIN_MISMATCH("IDA-BIA-001", "Biometric data - fgerMin did not match", "A0143"),
	FGRIMG_MISMATCH("IDA-BIA-002", "Biometric data - fgerImg did not match", "A0144"),
	IRISIMG_MISMATCH("IDA-BIA-015", "Biometric data - IrisImg did not match", "A0148"),
	INVALID_SIGNATURE("IDA-TSA-001", "Digital signature verification failed"),
	INVALID_CERTIFICATE("IDA-TSA-001", "Invalid certificate used in digital signature"),
	
	// OTP Generation
	VAL_KEY_NOT_FOUND_OTP_NOT_GENERATED("KER-OTV-005","Validation can't be performed against this key. Generate OTP first."),
	
	//Static Pin Store
	STATICPIN_NOT_STORED_PINVAUE("IDA-SPA-003","Could not store the static pin of the individual");

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
