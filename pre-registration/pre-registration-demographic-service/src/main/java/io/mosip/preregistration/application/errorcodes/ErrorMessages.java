/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.errorcodes;

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
	UNABLE_TO_CREATE_THE_PRE_REGISTRATION,

	/**
	 * ErrorMessage for PRG_PAM_APP_002
	 */
	PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE,

	/**
	 * ErrorMessage for PRG_PAM_APP_003
	 */
	DELETE_OPERATION_NOT_ALLOWED,

	/**
	 * ErrorMessage for PRG_PAM_APP_004
	 */
	FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD,

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	UNABLE_TO_FETCH_THE_PRE_REGISTRATION,

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	NO_RECORD_FOUND_FOR_USER_ID,

	/**
	 * ErrorMessage for PRG_PAM_APP_006
	 */
	INVAILD_STATUS_CODE,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_VALIDATION_FAILED,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_PARSING_FAILED,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_HTTP_REQUEST_EXCEPTION,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_VALIDATION_PROCESSING_EXCEPTION,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_IO_EXCEPTION,

	/**
	 * ErrorMessage for PRG_PAM_APP_007
	 */
	JSON_SCHEMA_IO_EXCEPTION,

	/**
	 * ErrorMessage for PRG_PAM_APP_008
	 */
	UNABLE_TO_UPDATE_THE_PRE_REGISTRATION,

	/**
	 * ErrorMessage for PRG_PAM_APP_009
	 */
	FILE_IO_EXCEPTION,

	/**
	 * ErrorMessage for PRG_PAM_APP_009
	 */
	UNSUPPORTED_ENCODING_CHARSET,

	/**
	 * ErrorMessage for PRG_PAM_DOC_015
	 */
	DOCUMENT_FAILED_TO_DELETE,

	/**
	 * ErrorMessage for PRG_PAM_APP_011
	 */
	UNSUPPORTED_DATE_FORMAT,

	/**
	 * ErrorMessage for PRG_PAM_APP_012
	 */
	MISSING_REQUEST_PARAMETER,

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	INVALID_PRE_REGISTRATION_ID,

	/**
	 * ErrorMessage for PRG_PAM_APP_005
	 */
	INVALID_STATUS_CODE,
	
	/**
	 * PRG_PAM_APP_005
	 */
	RECORD_NOT_FOUND_FOR_DATE_RANGE;
	;
}
