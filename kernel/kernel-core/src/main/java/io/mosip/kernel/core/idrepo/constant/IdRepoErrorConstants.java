package io.mosip.kernel.core.idrepo.constant;

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
	IDENTITY_HASH_MISMATCH("KER-IDR-003", "Identity Element hash does not match"),

	DOCUMENT_HASH_MISMATCH("KER-IDR-004", "Biometric/Document hash does not match"),

	/** The invalid uin. */
	INVALID_UIN("KER-IDR-005", "Invalid UIN"),

	/** The data validation failed. */
	DATA_VALIDATION_FAILED("KER-IDR-006", "Input Data Validation Failed"),

	/** The invalid request. */
	INVALID_REQUEST("KER-IDR-007", "Invalid Request"),

	/** The unknown error. */
	UNKNOWN_ERROR("KER-IDR-008", "Unknown error occured"),

	/** The database access error. */
	DATABASE_ACCESS_ERROR("KER-IDR-009", "Error occured while performing DB operations"),

	/** The record exists. */
	RECORD_EXISTS("KER-IDR-010", "Record already exists in DB"),

	/** The internal server error. */
	ENCRYPTION_DECRYPTION_FAILED("KER-IDR-011", "Failed to encrypt/decrypt message using Kernel Crypto Manager"),

	/** The no record found. */
	NO_RECORD_FOUND("KER-IDR-012", "No Record(s) found"),

	FILE_STORAGE_ACCESS_ERROR("KER-IDR-013", "Failed to store/retrieve files in DFS"),

	JSON_PROCESSING_FAILED("KER-IDR-014", "Failed to parse/process json"),

	JSON_SCHEMA_PROCESSING_FAILED("KER-IDR-015", "Unable to process id object json schema"),

	JSON_SCHEMA_RETRIEVAL_FAILED("KER-IDR-016", "Unable to retrieve id object schema from server"),

	CLIENT_ERROR("KER-IDR-017", "4XX - Client Error occured"),

	SERVER_ERROR("KER-IDR-018", "5XX - Server Error occured"),

	CONNECTION_TIMED_OUT("KER-IDR-019", "Connection timed out"),

	FILE_NOT_FOUND("KER-IDR-020", "File(s) not found in DFS"),

	UNAUTHORIZED("KER-IDR-021", "Unauthorized");
	
	/** The error code. */
	private final String errorCode;

	/** The error message. */
	private final String errorMessage;

	/**
	 * Constructor for {@link IdAuthenticationErrorConstants}.
	 *
	 * @param errorCode    - id-usage error codes which follows
	 *                     "<product>-<component>-<number>" pattern
	 * @param errorMessage - short error message
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
