package io.mosip.kernel.core.util.constant;

/**
 * Defines constants used in StringUtil.class
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum StringUtilConstants {

	MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE("KER-UTL-501","Array Index out of bounds"), MOSIP_PATTERN_SYNTAX_ERROR_CODE(
			"KER-UTL-503","Pattern Syntax Exception"), MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE("KER-UTL-502","Illegal Argument Exception");
	public final String errorCode;
	public final String errorMessage;

	StringUtilConstants(String string1,String string2) {
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
