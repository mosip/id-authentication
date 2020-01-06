package io.mosip.preregistration.datasync.errorcodes;

/**
 * This class is used to define Error messages for data sync and reverse data
 * sync
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 */
public enum ErrorMessages {
	/**
	 * ErrorMessage for PRG_DATA_SYNC_005
	 */
	FAILED_TO_CREATE_A_ZIP_FILE("Unable to create zip file"),
	/**
	 * Success message for reverse data sync
	 */
	PRE_REGISTRATION_IDS_STORED_SUCESSFULLY("Preregistration ids saved successfully"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_012
	 */
	FAILED_TO_STORE_PRE_REGISTRATION_IDS("Failed to store prereg id"),
	/**
	 * PRG_DATA_SYNC_001
	 */
	RECORDS_NOT_FOUND_FOR_DATE_RANGE("no data found for the requested date range"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_007
	 */
	DEMOGRAPHIC_GET_RECORD_FAILED("Demographic record failed to fetch"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_009
	 */
	INVALID_REGISTRATION_CENTER_ID("registration center id is invalid"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_011
	 */
	INVALID_REQUESTED_PRE_REG_ID_LIST("requested preregistration ids are not valid"),
	/**
	 * PRG_DATA_SYNC_006
	 */
	FAILED_TO_FETCH_DOCUMENT("unable to fetch the document"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_014
	 */
	FILE_IO_EXCEPTION("file IO exception"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_016
	 */
	BOOKING_NOT_FOUND("booking data not found"),
	/**
	 * ErrorMessage for PRG_DATA_SYNC_017
	 */
	ERROR_WHILE_PARSING("Error occured while parsing data");

	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}
