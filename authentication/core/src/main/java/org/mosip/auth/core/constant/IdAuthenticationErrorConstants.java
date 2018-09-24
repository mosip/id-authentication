/**
 * 
 */
package org.mosip.auth.core.constant;

/**
 * List of all IDA error codes and its respective error messages.
 * 
 * @author Manoj SP
 *
 */
public enum IdAuthenticationErrorConstants {
	ID_VERIFICATION_FAILED_EXCEPTION("IDA-DTV-IDV-001", "Invalid UIN"),
	ID_MAPPING_FAILED_EXCEPTION("IDA-DTV-IDV-002", "VID is not present or not matched"),
	ID_EXPIRED_EXCEPTION("IDA-DTV-IDV-003", "Expired VID is expired"),
	
	INVALID_IDTYPE("IDA-DTV-IDV-005","Invalid Id-Type"),
	INVALID_GENERATE_OTP_REQUEST("IDA-DTV-RQV-001", "Invalid OTP request"),
	
	OTP_GENERATION_REQUEST_FAILED("IDA-DTV-RQV-002", "OTP generation request failed"),
	OTP_VALDIATION_REQUEST_FAILED("IDA-DTV-IDV-005", "OTP Validation Request Failed"),
	OTP_KEY_INVALID("IDA-DTV-IDV-006", "OTP Key is Null or Invalid"),
	
	EXPIRED_OTP_REQUEST_TIME("IDA-DTV-RQV-005","OTP requested time expired"),
	
	REFID_INVALID("IDA-DTV-IDV-008", "Reference ID is Null or Invalid"),
	UIN_INVALID("IDA-DTV-IDV-009", "UIN is Null or Invalid"), KEY_INVALID("IDA-DTV-IDV-010", "Key is Null or Invalid"),
	TXNID_INVALID("IDA-DTV-IDV-010", "Transaction ID is Null or Invalid"),
	TEMPID_INVALID("IDA-DTV-RQV-003", "Temporavary ID is Null or Invalid"),
	DATA_VALIDATION_FAILED("IDA-DTV-RQV-004", "Demographic data is incorrect"),
	ID_INVALID_VALIDATEOTP_REQUEST("IDA-DTV-ARV-001", "Invalid Validate OTP request"),
	ID_OTPVALIDATION_REQUEST_FAILED("IDA-DTV-ARV-001", "Validate OTP request failed"),
	INVALID_AUTH_REQUEST("IDA-DTV-RQV-101", "Invalid Auth Request"),
	UNKNOWN_ERROR("IDA-DTV-IDV-004", "Unknown error occured"),
	INVALID_URI("IDA-RST-DTV-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-DTV-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-DTV-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-DTV-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-DTV-005", "Timeout is invalid"),
	CLIENT_ERROR("IDA-RST-UTL-001", "4XX - Client Error occured"),
	SERVER_ERROR("IDA-RST-UTL-002", "5XX - Server Error occured"),
	CONNECTION_TIMED_OUT("IDA-RST-UTL-003", "Connection timed out"), 
	
	INVALID_UIN("IDA-IDM-UIN-301", "Invalid UIN"),
	INACTIVE_UIN("IDA-IDM-UIN-302", "Inactive UIN"),
	
	INVALID_VID("IDA-IDM-VID-303", "Invalid VID"),
	EXPIRED_VID("IDA-IDM-VID-304", "Expired VID"),
	INACTIVE_VID("IDA-IDM-VID-302", "Inactive VID"),
	
	INVALID_UIN_BUISNESS("IDA-IDMB-UIN-401", "Invalid UIN"),
	INVALID_VID_BUISNESS("IDA-IDMB-VID-402", "Invalid VID"), 
	AUTHENTICATION_FAILED("IDA-IDMC-AUT-501", "Authentication failed or Invalid UIN/VID"),
	
	INCORRECT_IDTYPE("IDA-DTV-IDT-601","INCORRECT IDTYPE");

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
