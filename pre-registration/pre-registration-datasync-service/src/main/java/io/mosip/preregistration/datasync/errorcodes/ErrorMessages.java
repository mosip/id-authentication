package io.mosip.preregistration.datasync.errorcodes;

/**
 * @author M1046129
 *
 */
public enum ErrorMessages {
	DOCUMENT_NOT_PRESENT_REQUEST("Document not found"), 
	DOCUMENT_IS_MISSING("Document is missing"), 
	DOCUMENT_TABLE_NOTACCESSIBLE("unable to access the document table"), 
	FAILED_TO_CREATE_A_ZIP_FILE("Unable to create zip file"), 
	RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID("No records found for requested prereg id"), 
	PRE_REGISTRATION_IDS_STORED_SUCESSFULLY("Preregistration ids saved successfully"), 
	FAILED_TO_STORE_PRE_REGISTRATION_IDS("Failed to store prereg id"),
	RECORDS_NOT_FOUND_FOR_DATE_RANGE("no data found for the requested date range"),
	RECORDS_NOT_FOUND_FOR_REGISTRATION_CENTER("no data found for the requested registration center"),
	INVALID_USER_ID("user id is not valid"),
	DEMOGRAPHIC_GET_RECORD_FAILED("Demographic record failed to fetch"),
	DOCUMENT_GET_RECORD_FAILED("Document record failed to fetch"),
	INVALID_REGISTRATION_CENTER_ID(""),
	INVALID_REQUESTED_DATE(""),
	INVALID_REQUESTED_CREATED_DATE(""),
	INVALID_REQUESTED_UPDATED_DATE(""),
	INVALID_REQUESTED_PRE_REG_ID_LIST("requested preregistration ids are not valid"),
	 FAILED_TO_GET_PRE_REG_ID_BY_REG_CLIENT_ID("unable to fetch the preregistration ids for requested registration center id"),
	 FAILED_TO_FETCH_DOCUMENT("unable to fetch the document"),
		/**
		 * ErrorMessage for PRG_PAM_APP_009
		 */
		FILE_IO_EXCEPTION("file IO exception"),

		/**
		 * ErrorMessage for PRG_PAM_APP_009
		 */
		UNSUPPORTED_ENCODING_CHARSET("unsupported encoding charset"),
		FILE_NOT_FOUND("file not found"),
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
