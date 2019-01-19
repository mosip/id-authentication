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

	/** The unsupported lang code. */
	UNSUPPORTED_LANG_CODE("KER-IDR-005", "Unsupported Language Code"),

	/** The invalid uin. */
	INVALID_UIN("KER-IDR-006", "Invalid UIN"),

	/** The data validation failed. */
	DATA_VALIDATION_FAILED("KER-IDR-007", "Input Data Validation Failed"),

	/** The invalid request. */
	INVALID_REQUEST("KER-IDR-008", "Invalid Request"),

	/** The unknown error. */
	UNKNOWN_ERROR("KER-IDR-009", "Unknown error occured"),

	/** The database access error. */
	DATABASE_ACCESS_ERROR("KER-IDR-010", "Error occured while performing DB operations"),

	/** The record exists. */
	RECORD_EXISTS("KER-IDR-011", "Record already exists in DB"),

	/** The internal server error. */
	ENCRYPTION_DECRYPTION_FAILED("KER-IDR-012", "Failed to either encrypt/decrypt message using Kernel Crypto Manager"),

	/** The no record found. */
	NO_RECORD_FOUND("KER-IDR-013", "No Record(s) found"),

	FILE_STORAGE_ACCESS_ERROR("KER-IDR-014", "Failed to store/retrieve files in DFS"), 
	
	JSON_PROCESSING_FAILED("KER-IDR-015", "Failed to parse/process json"),
	
	JSON_SCHEMA_PROCESSING_FAILED("KER-IDR-016", "Unable to process id object json schema"),
	
	JSON_SCHEMA_RETRIEVAL_FAILED("KER-IDR-017", "Unable to retrieve id object schema from server");

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
