package io.kernel.core.idrepo.constant;

/**
 * @author Manoj SP
 *
 */
public enum IdRepoErrorConstants {
    
	MISSING_INPUT_PARAMETER("KER-IDR-001", "Missing Input Parameter - %s"),
	INVALID_INPUT_PARAMETER("KER-IDR-002", "Invalid Input Parameter - %s"),
	IDENTITY_MISMATCH("KER-IDR-003", "One or more Identity Element entered does not match MOSIP ID"),
	UNSUPPORTED_LANG_CODE("KER-IDR-004", "Unsupported Language Code"),
	INVALID_UIN("KER-IDR-005", "Invalid UIN"),
	DATA_VALIDATION_FAILED("KER-IDR-006", "Input Data Validation Failed"),
	INVALID_REQUEST("KER-IDR-007", "Invalid UIN"),
	UNKNOWN_ERROR("KER-IDR-008", "Unknown error occured"),
	CONNECTION_TIMED_OUT("KER-IDR-009", "Connection Timed out"),
	DATABASE_ACCESS_ERROR("KER-IDR-010", "Error occured while performing DB operations"),
	RECORD_EXISTS("KER-IDR-011", "Record already exists in DB"),
	NON_REGISTERED_UIN("KER-IDR-012", "Request UIN is %s, cannot retrieve/update this record."),
	INTERNAL_SERVER_ERROR("KER-IDR-013", "Internal Server Error"),
	NO_RECORD_FOUND("KER-IDR-014", "No Record(s) found");

	private final String errorCode;
	private final String errorMessage;

	/**
	 * Constructor for {@link IdAuthenticationErrorConstants}
	 * 
	 * @param errorCode    - id-usage error codes which follows
	 *                     "<product>-<module>-<component>-<number>" pattern
	 * @param errorMessage - short error message
	 */
	private IdRepoErrorConstants(String errorCode, String errorMessage) {
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
