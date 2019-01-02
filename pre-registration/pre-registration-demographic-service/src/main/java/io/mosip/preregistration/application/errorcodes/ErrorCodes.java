/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.errorcodes;

/**
 * 
 * This Enum provides the constant variables to define Error codes.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public enum ErrorCodes {

	/* ErrorCode for UNABLE_TO_CREATE_THE_PRE_REGISTRATION */
	PRG_PAM_APP_001,

	/* ErrorCode for PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE */
	PRG_PAM_APP_002,

	/* ErrorCode for DELETE_OPERATION_NOT_ALLOWED */
	PRG_PAM_APP_003,

	/* ErrorCode for FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD */
	PRG_PAM_APP_004,

	/* ErrorCode for UNABLE_TO_FETCH_THE_PRE_REGISTRATION & NO_RECORD_FOUND_FOR_USER_ID */
	PRG_PAM_APP_005,

	/* ErrorCode for INVAILD_STATUS_CODE */
	PRG_PAM_APP_006,

	/* ErrorCode for
	 * JSON_VALIDATION_FAILED, JSON_PARSING_FAILED, JSON_HTTP_REQUEST_EXCEPTION,
	 * JSON_VALIDATION_PROCESSING_EXCEPTION, JSON_IO_EXCEPTION &
	 * JSON_SCHEMA_IO_EXCEPTION
	 */
	PRG_PAM_APP_007,

	/* ErrorCode for UNABLE_TO_UPDATE_THE_PRE_REGISTRATION */
	PRG_PAM_APP_008,

	/* ErrorCode for FILE_IO_EXCEPTION 7 UNSUPPORTED_ENCODING_CHARSET */
	PRG_PAM_APP_009,

	/* ErrorCode for DOCUMENT_FAILED_TO_DELETE */
	PRG_PAM_DOC_015,

	/* ErrorCode for UNSUPPORTED_DATE_FORMAT */
	PRG_PAM_APP_011,

	/* ErrorCode for MISSING_REQUEST_PARAMETER */
	PRG_PAM_APP_012;
}
