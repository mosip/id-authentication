package io.mosip.kernel.core.util.constant;

/**
 * @author Priya Soni
 *
 */
public enum FileUtilConstants {
	FILE_EXISTS_ERROR_CODE("KER-UTL-001", "File already exists"),
	FILE_NOT_FOUND_ERROR_CODE("KER-UTL-002", "File Not Found"),
	ILLEGAL_ARGUMENT_ERROR_CODE("KER-UTL-003", "Illegal Argument passed"),
	IO_ERROR_CODE("KER-UTL-004", "Interrupted IO Operation"),
	NULL_POINTER_ERROR_CODE("KER-UTL-005", "Null Reference found"),
	UNSUPPORTED_CHARSET_ERROR_CODE("KER-UTL-006", "No support available for the requested charset"),
	UNSUPPORTED_ENCODING_ERROR_CODE("KER-UTL-007", "The Character Encoding is not supported");

	public final String errorCode;

	public final String message;

	FileUtilConstants(final String errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

}
