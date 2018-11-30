package io.kernel.core.idrepo.constant;

/**
 * The Enum IdRepoErrorConstants.
 *
 * @author Manoj SP
 */
public enum IdRepoErrorConstants {

	/** The missing input parameter. */
	MISSING_INPUT_PARAMETER("KER-IDR-001", "Missing Input Parameter - %s"),

	/** The invalid input parameter. */
	INVALID_INPUT_PARAMETER("KER-IDR-002", "Invalid Input Parameter - %s"),

	/** The identity mismatch. */
	IDENTITY_MISMATCH("KER-IDR-003", "One or more Identity Element entered does not match MOSIP ID"),

	/** The unsupported lang code. */
	UNSUPPORTED_LANG_CODE("KER-IDR-004", "Unsupported Language Code"),

	/** The invalid uin. */
	INVALID_UIN("KER-IDR-005", "Invalid UIN"),

	/** The data validation failed. */
	DATA_VALIDATION_FAILED("KER-IDR-006", "Input Data Validation Failed"),

	/** The invalid request. */
	INVALID_REQUEST("KER-IDR-007", "Invalid Request"),

	/** The unknown error. */
	UNKNOWN_ERROR("KER-IDR-008", "Unknown error occured"),

	/** The connection timed out. */
	CONNECTION_TIMED_OUT("KER-IDR-009", "Connection Timed out"),

	/** The database access error. */
	DATABASE_ACCESS_ERROR("KER-IDR-010", "Error occured while performing DB operations"),

	/** The record exists. */
	RECORD_EXISTS("KER-IDR-011", "Record already exists in DB"),

	/** The non registered uin. */
	NON_REGISTERED_UIN("KER-IDR-012", "Request UIN is %s, cannot retrieve/update this record."),

	/** The internal server error. */
	INTERNAL_SERVER_ERROR("KER-IDR-013", "Internal Server Error"),

	/** The no record found. */
	NO_RECORD_FOUND("KER-IDR-014", "No Record(s) found"),
	
	UIN_GENERATION_FAILED("KER-IDR-015", "Generation of UIN failed");

	/** The error code. */
	private final String errorCode;

	/** The error message. */
	private final String errorMessage;

	/**
	 * Constructor for {@link IdAuthenticationErrorConstants}.
	 *
	 * @param errorCode
	 *            - id-usage error codes which follows
	 *            "<product>-<component>-<number>" pattern
	 * @param errorMessage
	 *            - short error message
	 */
	private IdRepoErrorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 *
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 *
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
