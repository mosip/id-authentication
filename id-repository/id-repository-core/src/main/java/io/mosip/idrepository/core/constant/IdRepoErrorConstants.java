package io.mosip.idrepository.core.constant;

/**
 * The Enum IdRepoErrorConstants.
 *
 * @author Manoj SP
 */
public enum IdRepoErrorConstants {

	/** The missing input parameter. */
	MISSING_INPUT_PARAMETER("IDR-IDS-001", "Missing Input Parameter - %s"),

	/** The invalid input parameter. */
	INVALID_INPUT_PARAMETER("IDR-IDS-002", "Invalid Input Parameter - %s"),

	/** The identity mismatch. */
	IDENTITY_HASH_MISMATCH("IDR-IDS-003", "Identity Element hash does not match"),

	DOCUMENT_HASH_MISMATCH("IDR-IDS-004", "Biometric/Document hash does not match"),

	/** The data validation failed. */
	DATA_VALIDATION_FAILED("IDR-IDS-006", "Input Data Validation Failed"),

	/** The invalid request. */
	INVALID_REQUEST("IDR-IDS-007", "Invalid Request"),

	/** The unknown error. */
	UNKNOWN_ERROR("IDR-IDS-008", "Unknown error occured"),

	/** The database access error. */
	DATABASE_ACCESS_ERROR("IDR-IDS-009", "Error occured while performing DB operations"),

	/** The record exists. */
	RECORD_EXISTS("IDR-IDS-010", "Record already exists in DB"),

	/** The internal server error. */
	ENCRYPTION_DECRYPTION_FAILED("IDR-IDS-011", "Failed to encrypt/decrypt message using Kernel Crypto Manager"),

	/** The no record found. */
	NO_RECORD_FOUND("IDR-IDS-012", "No Record(s) found"),

	FILE_STORAGE_ACCESS_ERROR("IDR-IDS-013", "Failed to store/retrieve files in DFS"),

	JSON_PROCESSING_FAILED("IDR-IDS-014", "Failed to parse/process json"),

	JSON_SCHEMA_PROCESSING_FAILED("IDR-IDS-015", "Unable to process id object json schema"),

	JSON_SCHEMA_RETRIEVAL_FAILED("IDR-IDS-016", "Unable to retrieve id object schema from server"),

	CLIENT_ERROR("IDR-IDS-017", "4XX - Client Error occured"),

	SERVER_ERROR("IDR-IDS-018", "5XX - Server Error occured"),

	CONNECTION_TIMED_OUT("IDR-IDS-019", "Connection timed out"),

	FILE_NOT_FOUND("IDR-IDS-020", "File(s) not found in DFS"),

	AUTHORIZATION_FAILED("IDR-IDS-021", "Authorization Failed"),
	
	NO_RECORD_FOUND_VID("IDR-VID-006", "No Record(s) found"),
	
	INVALID_VID("IDR-VID-002","%s VID"),
	
	INVALID_INPUT_PARAMETER_VID("IDR-VID-001","Invalid Input Parameter - %s"),
	/** The missing input parameter. */
	MISSING_INPUT_PARAMETER_VID("IDR-VID-007", "Missing Input Parameter - %s");
	

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
