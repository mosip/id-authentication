package io.mosip.kernel.core.util.constant;

/**
 * This enum contains all the error codes and messages of Jsonutil class
 * 
 * @author Sidhant Agarwal
 *
 */
public enum JsonUtilConstants {

	MOSIP_IO_EXCEPTION_ERROR_CODE("KER-UTL-101", "File not found"),
	MOSIP_JSON_GENERATION_ERROR_CODE("KER-UTL-102", "Json not generated successfully"),
	MOSIP_JSON_MAPPING_ERROR_CODE("KER-UTL-103", "Json mapping Exception"),
	MOSIP_JSON_PARSE_ERROR_CODE("KER-UTL-104", "Json not parsed successfully"),
	MOSIP_JSON_PROCESSING_EXCEPTION("KER-UTL-105", "json not processed successfully");
	public final String errorCode;
	public final String errorMessage;

	JsonUtilConstants(String string1, String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
