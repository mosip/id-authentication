package io.mosip.idrepository.core.constant;

/**
 * The Enum IdRepoErrorConstants.
 *
 * @author Manoj SP
 */
public enum IdRepoErrorConstants {
	
	//	IdRepo Core

	/** The missing input parameter. */
	MISSING_INPUT_PARAMETER("IDR-IDC-001", "Missing Input Parameter - %s"),

	/** The invalid input parameter. */
	INVALID_INPUT_PARAMETER("IDR-IDC-002", "Invalid Input Parameter - %s"),
	
	/** The invalid request. */
	INVALID_REQUEST("IDR-IDC-003", "Invalid Request"),
	
	/** The unknown error. */
	UNKNOWN_ERROR("IDR-IDC-004", "Unknown error occured"),
	
	/** The data validation failed. */
	DATA_VALIDATION_FAILED("IDR-IDC-005", "Input Data Validation Failed"),
	
	/** The database access error. */
	DATABASE_ACCESS_ERROR("IDR-IDC-006", "Error occured while performing DB operations"),
	
	/** The no record found. */
	NO_RECORD_FOUND("IDR-IDC-007", "No Record(s) found"),
	
	CLIENT_ERROR("IDR-IDC-008", "4XX - Client Error occured"),
	
	SERVER_ERROR("IDR-IDC-009", "5XX - Server Error occured"),
	
	CONNECTION_TIMED_OUT("IDR-IDC-010", "Connection timed out"),
	
	AUTHORIZATION_FAILED("IDR-IDC-011", "Authorization Failed"),

	// Identity Service
	
	/** The identity mismatch. */
	IDENTITY_HASH_MISMATCH("IDR-IDS-003", "Identity Element hash does not match"),

	DOCUMENT_HASH_MISMATCH("IDR-IDS-004", "Biometric/Document hash does not match"),

	/** The record exists. */
	RECORD_EXISTS("IDR-IDS-010", "Record already exists in DB"),

	/** The internal server error. */
	ENCRYPTION_DECRYPTION_FAILED("IDR-IDS-011", "Failed to encrypt/decrypt message using Kernel Crypto Manager"),

	FILE_STORAGE_ACCESS_ERROR("IDR-IDS-013", "Failed to store/retrieve files in DFS"),

	ID_OBJECT_PROCESSING_FAILED("IDR-IDS-014", "Failed to parse/process Identity"),

	ID_OBJECT_SCHEMA_PROCESSING_FAILED("IDR-IDS-015", "Unable to process id object schema"),

	ID_OBJECT_SCHEMA_RETRIEVAL_FAILED("IDR-IDS-016", "Unable to retrieve id object schema from server"),

	FILE_NOT_FOUND("IDR-IDS-020", "File(s) not found in DFS"),
	
	// VID Service
	
	INVALID_VID("IDR-VID-002","%s VID"),
	
	VID_GENERATION_FAILED("IDR-VID-003","Failed to %s VID"),
	
	VID_POLICY_FAILED("IDR-VID-005","Could not generate/regenerate VID as per policy"),
	
	INVALID_UIN("IDR-VID-008","%s UIN"),
	
	UIN_RETRIEVAL_FAILED("IDR-VID-009", "Failed to retrieve uin data using Identity Service"),
	
	UIN_HASH_MISMATCH("IDR-IDS-010", "Uin hash does not match");
	

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
