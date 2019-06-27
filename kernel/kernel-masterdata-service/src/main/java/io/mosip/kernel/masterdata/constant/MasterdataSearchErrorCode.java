package io.mosip.kernel.masterdata.constant;

/**
 * Error codes for masterdata search
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum MasterdataSearchErrorCode {
	NULLENTITY_MSG("KER-MSD-XX1","entity must not be empty"),
	INVALID_COLUMN("KER-MSD-XX2","Invalid column : %s"),
	INVALID_PAGINATION_VALUE("KER-MSD-XX4","invalid pagination page:%d and size:%d"),
	FILTER_TYPE_NOT_AVAILABLE("KER-MSD-XX5","Filter type is missing"),
	MISSING_FILTER_COLUMN("KER-MSD-XX6","Filter column is missing"),
	INVALID_SORT_INPUT("KER-MSD-XX6","Missing sort field or sort type values"),
	INVALID_BETWEEN_VALUES("KER-MSD-XX7","Invalid fromValue or toValue values");
	/**
	 * The error code.
	 */
	private final String errorCode;
	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * Constructor for MasterdataSearchErrorCode.
	 * 
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
	 */
	private MasterdataSearchErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
