package io.mosip.preregistration.datasync.errorcodes;

/**
 * @author M1046129
 *
 */
public enum ErrorMessages {
	DOCUMENT_IS_MISSING("Document is missing"), 
	FAILED_TO_CREATE_A_ZIP_FILE("Unable to create zip file"), 
	PRE_REGISTRATION_IDS_STORED_SUCESSFULLY("Preregistration ids saved successfully"), 
	FAILED_TO_STORE_PRE_REGISTRATION_IDS("Failed to store prereg id"),
	RECORDS_NOT_FOUND_FOR_DATE_RANGE("no data found for the requested date range"),
	RECORDS_NOT_FOUND_FOR_REGISTRATION_CENTER("no data found for the requested registration center"),
	DEMOGRAPHIC_GET_RECORD_FAILED("Demographic record failed to fetch"),
	DOCUMENT_GET_RECORD_FAILED("Document record failed to fetch"),
	INVALID_REGISTRATION_CENTER_ID("registration center id is invalid"),
	INVALID_REQUESTED_DATE("requested date is invalid"),
	INVALID_REQUESTED_PRE_REG_ID_LIST("requested preregistration ids are not valid"),
	FAILED_TO_FETCH_DOCUMENT("unable to fetch the document"),
		/**
		 * ErrorMessage for PRG_PAM_APP_009
		 */
		FILE_IO_EXCEPTION("file IO exception"),

		/**
		 * ErrorMessage for PRG_DATA_SYNC_016
		 */
		BOOKING_NOT_FOUND ("booking data not found"),
		ERROR_WHILE_PARSING("Error occured while parsing data")//PRG_DATA_SYNC_017
	;
	
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
