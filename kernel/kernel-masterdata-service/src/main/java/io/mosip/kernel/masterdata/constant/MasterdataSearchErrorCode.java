package io.mosip.kernel.masterdata.constant;

/**
 * Error codes for masterdata search
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum MasterdataSearchErrorCode {
	INVALID_COLUMN("KER-MSD-310","Invalid column received : %s"),
	INVALID_PAGINATION_VALUE("KER-MSD-313","Invalid pagination value received pagestart:%d and pagefetch:%d"),
	FILTER_TYPE_NOT_AVAILABLE("KER-MSD-312","Filter type is missing"),
	MISSING_FILTER_COLUMN("KER-MSD-311","Column is missing in request"),
	INVALID_SORT_INPUT("KER-MSD-314","Missing sort field or sort type values"),
	INVALID_BETWEEN_VALUES("KER-MSD-315","Invalid fromValue or toValue"),
	INVALID_PAGINATION("KER-MSD-356","Pagination cannot be null"),
	INVALID_SORT_TYPE("KER-MSD-358","Sort type %s is not supported"),
	ERROR_OCCURED_WHILE_SORTING("KER-MSD-359","Error occured while sorting"),
	INVALID_SORT_FIELD("KER-MSD-357","Invalid sort field %s "),
	INVALID_VALUE("KER-MSD-390","Invalid filter value");
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
