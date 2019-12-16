/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.errorcodes;

/**
 * 
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum ErrorMessages {

	/**
	 * ErrorMessage for PRG_PAM_APP_001
	 */
	UNABLE_TO_CREATE_THE_PRE_REGISTRATION("Failed to create the pre-registration with demographic data provided"),

	/**
	 * ErrorMessage for PRG_PAM_APP_002
	 */
	PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE("unable to access the pre-registration table"),

	/**
	 * ErrorMessage for PRG_PAM_APP_003
	 */
	DELETE_OPERATION_NOT_ALLOWED("delete operation is not allowed"),

	/**
	 * ErrorMessage for PRG_PAM_APP_004
	 */
	FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD("failed to delete the pre-registration data"),

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	UNABLE_TO_FETCH_THE_PRE_REGISTRATION("No data found for the requested pre-registration id"),

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	NO_RECORD_FOUND_FOR_USER_ID("No record found for the requested user id"),

	/**
	 * ErrorMessage for PRG_PAM_APP_006
	 */
	INVAILD_STATUS_CODE("status code is invalid"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_VALIDATION_FAILED("json validation is failed"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_PARSING_FAILED("json parsing is failed"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_HTTP_REQUEST_EXCEPTION("json Http request exception"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_VALIDATION_PROCESSING_EXCEPTION("json validation processing exception"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_IO_EXCEPTION("json IO exception"),

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_SCHEMA_IO_EXCEPTION("json schema IO exception"),

	/**
	 * ErrorMessage for PRG_PAM_APP_008
	 */
	UNABLE_TO_UPDATE_THE_PRE_REGISTRATION("Failed to update the requested preregistration data"),

	/**
	 * ErrorMessage for PRG_PAM_APP_009
	 */
	FILE_IO_EXCEPTION("file IO exception"),

	/**
	 * ErrorMessage for PRG_PAM_APP_009
	 */
	UNSUPPORTED_ENCODING_CHARSET("unsupported encoding charset"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_015
	 */
	DOCUMENT_FAILED_TO_DELETE("failed to delete the document"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_016
	 */
	BOOKING_FAILED_TO_DELETE("failed to delete the booking"),

	/**
	 * ErrorMessage for PRG_PAM_APP_011
	 */
	UNSUPPORTED_DATE_FORMAT("date format is not supported"),

	/**
	 * ErrorMessage for PRG_PAM_APP_012
	 */
	MISSING_REQUEST_PARAMETER("request parameter is missing"),

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	INVALID_STATUS_CODE("status code is invalid"),

	/**
	 * PRG_PAM_APP_005
	 */
	RECORD_NOT_FOUND_FOR_DATE_RANGE("no record found for the requested date range"),

	/**
	 * PRG_PAM_APP_013
	 */
	RECORD_NOT_FOUND("no record found"),

	/**
	 * PRG_PAM_APP_014
	 */
	DOCUMENT_SERVICE_FAILED_TO_CALL("Document service rest call failed"),

	/**
	 * PRG_PAM_DOC_005
	 */
	DOCUMENT_IS_MISSING("no document found for the requested pre-registration id"),

	/**
	 * PRG_PAM_APP_016
	 */
	PAGE_NOT_FOUND("no record found for the requested page index"),

	/**
	 * PRG_PAM_APP_015
	 */
	PAGE_SIZE_MUST_BE_GREATER_THAN_ZERO("Page size must be greater than zero"),

	/**
	 * PRG_PAM_APP_017
	 */
	INVALID_PREID_FOR_USER("Requested preregistration id does not belong to the user"),
	/**
	 * PRG_PAM_APP_018
	 */
	UBALE_TO_READ_IDENTITY_JSON("Failed to read the identity json from the server"),
	
	/**
	 * PRG_PAM_APP_020
	 */
	PRID_RESTCALL_FAIL("Rest call to get prid failed"),
	
	/**
	 * PRG_PAM_APP_019
	 */
	INVALID_PAGE_INDEX_VALUE("Invalid page index value"),
	
	/**
	 * PRG_PAM_APP_021
	 */
	DUPLICATE_KEY("Duplicate key for prid");

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
