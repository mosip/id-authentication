package io.mosip.kernel.masterdata.constant;

/**
 * Search filter validation error constants
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum ValidationErrorCode {
	NO_FILTER_FOUND("KER-MSD-316", "Column %s doesn't support filter"), 
	COLUMN_DOESNT_EXIST("KER-MSD-317","Column %s doesn't exist for the searched entity"), 
	FILTER_NOT_SUPPORTED("KER-MSD-318","Column %s doesn't support filter type %s"), 
	INVALID_COLUMN_VALUE("KER-MSD-319","Column value is null or empty"),
	NO_FILTER_COLUMN_FOUND("-----1----", "Type can't be empty or null"), 
	FILTER_COLUMN_DOESNT_EXIST("----2-----","Type %s doesn't exist for the searched entity"), 
	FILTER_COLUMN_NOT_SUPPORTED("-----3----","Type doesn't support filter %s");

	/**
	 * Error Code
	 */
	private final String errorCode;
	/**
	 * Error Message
	 */
	private final String errorMessage;

	/**
	 * Constructor to initialize
	 * 
	 * @param errorCode
	 *            validation error code
	 * @param errorMessage
	 *            validation error message
	 */
	private ValidationErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Method to fetch error code
	 * 
	 * @return error Code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Method to fetch error message
	 * 
	 * @return error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
