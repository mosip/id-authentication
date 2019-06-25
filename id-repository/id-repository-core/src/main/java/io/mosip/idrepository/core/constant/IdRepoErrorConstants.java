package io.mosip.idrepository.core.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Enum IdRepoErrorConstants - contains error codes and messages thrown by
 * the application.
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
	
	/** The client error. */
	CLIENT_ERROR("IDR-IDC-008", "4XX - Client Error occured"),
	
	/** The server error. */
	SERVER_ERROR("IDR-IDC-009", "5XX - Server Error occured"),
	
	/** The connection timed out. */
	CONNECTION_TIMED_OUT("IDR-IDC-010", "Connection timed out"),
	
	/** The authorization failed. */
	AUTHORIZATION_FAILED("IDR-IDC-011", "Authorization Failed"),
	
	/** The record exists. */
	RECORD_EXISTS("IDR-IDC-012", "Record already exists in DB"),

	// Identity Service
	
	/** The identity mismatch. */
	IDENTITY_HASH_MISMATCH("IDR-IDS-001", "Identity Element hash does not match"),

	/** The document hash mismatch. */
	DOCUMENT_HASH_MISMATCH("IDR-IDS-002", "Biometric/Document hash does not match"),

	/** The internal server error. */
	ENCRYPTION_DECRYPTION_FAILED("IDR-IDS-003", "Failed to encrypt/decrypt message using Kernel Crypto Manager"),

	/** The file storage access error. */
	FILE_STORAGE_ACCESS_ERROR("IDR-IDS-004", "Failed to store/retrieve files in DFS"),

	/** The id object processing failed. */
	ID_OBJECT_PROCESSING_FAILED("IDR-IDS-005", "Failed to process Id Object using kernel Id Object validator"),

	/** The file not found. */
	FILE_NOT_FOUND("IDR-IDS-006", "File(s) not found in DFS"),
	
	/** The masterdata retrieve error. */
	MASTERDATA_RETRIEVE_ERROR("IDR-IDS-007", "Failed to retrieve data from kernel Masterdata"),
	
	// VID Service
	
	/** The invalid vid. */
	INVALID_VID("IDR-VID-001","VID is %s"),
	
	/** The vid generation failed. */
	VID_GENERATION_FAILED("IDR-VID-002","Failed to generate VID"),
	
	/** The vid policy failed. */
	VID_POLICY_FAILED("IDR-VID-003","Could not generate/regenerate VID as per policy"),
	
	/** The invalid uin. */
	INVALID_UIN("IDR-VID-004","%s UIN"),
	
	/** The uin retrieval failed. */
	UIN_RETRIEVAL_FAILED("IDR-VID-005", "Failed to retrieve uin data using Identity Service"),
	
	/** The uin hash mismatch. */
	UIN_HASH_MISMATCH("IDR-VID-006", "Uin hash does not match");
	

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
	
	/**
	 * Gets the all error codes.
	 *
	 * @return the all error codes
	 */
	public static List<String> getAllErrorCodes() {
		return Collections.unmodifiableList(Arrays.asList(IdRepoErrorConstants.values()).parallelStream()
				.map(IdRepoErrorConstants::getErrorCode).collect(Collectors.toList()));
	}
}
