package io.mosip.preregistration.datasync.errorcodes;

/**
 * This class is used to define Error codes for data sync and reverse data sync
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 */
public enum ErrorCodes {

	PRG_DATA_SYNC_001("PRG_DATA_SYNC_001"), // Records Not Found For Requested Date Range
	PRG_DATA_SYNC_002("PRG_DATA_SYNC_002"), // Records Not Found For Requested Registration Center
	PRG_DATA_SYNC_003("PRG_DATA_SYNC_003"), // Invalid User Id
	PRG_DATA_SYNC_004("PRG_DATA_SYNC_004"), // Records Not Found For Requested PreRegId
	PRG_DATA_SYNC_005("PRG_DATA_SYNC_005"), // Failed to create a zip file
	PRG_DATA_SYNC_006("PRG_DATA_SYNC_006"), // FAILED_TO_FETCH_DOCUMENT
	PRG_DATA_SYNC_007("PRG_DATA_SYNC_007"), // DEMOGRAPHIC_GET_STATUS_FAILED
	PRG_DATA_SYNC_009("PRG_DATA_SYNC_009"), // INVALID_REGISTRATION_CENTER_ID
	PRG_DATA_SYNC_010("PRG_DATA_SYNC_010"), // INVALID_REQUESTED_DATE
	PRG_DATA_SYNC_011("PRG_DATA_SYNC_011"), // INVALID_REQUESTED_PRE_REG_ID_LIST
	PRG_DATA_SYNC_012("PRG_DATA_SYNC_012"), // FAILED_TO_STORE_PRE_REGISTRATION_IDS
	PRG_DATA_SYNC_013("PRG_DATA_SYNC_013"), // FAILED_TO_GET_PRE_REG_ID_BY_REG_CLIENT_ID
	PRG_DATA_SYNC_014("PRG_DATA_SYNC_014"), // FILE_IO_EXCEPTION
	PRG_DATA_SYNC_016("PRG_DATA_SYNC_016"), // BOOKING_NOT_FOUND
	PRG_DATA_SYNC_017("PRG_DATA_SYNC_017"); // PARSING_FAILED

	/**
	 * @param code
	 */
	private ErrorCodes(String code) {
		this.code = code;
	}

	/**
	 * Code
	 */
	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
