package io.mosip.kernel.masterdata.constant;

/**
 * Search filter validation error constants
 * 
 * @author Abhishek Kumar
 * @author Megha Tanga
 * @since 1.0.0
 */
public enum ValidationErrorCode {
	NO_FILTER_FOUND("KER-MSD-316", "Column %s doesn't support filter"), 
	COLUMN_DOESNT_EXIST("KER-MSD-317","Column %s doesn't exist for the searched entity"), 
	FILTER_NOT_SUPPORTED("KER-MSD-318","Column %s doesn't support filter type %s"), 
	INVALID_COLUMN_VALUE("KER-MSD-319","Column value is null or empty"),
	NO_FILTER_COLUMN_FOUND("KER-MSD-322", "Filter Type cannot be empty or null"), 
	FILTER_COLUMN_DOESNT_EXIST("KER-MSD-323","Filter Type for column %s is not supported"), 
	FILTER_COLUMN_NOT_SUPPORTED("KER-MSD-324","Received Filter Type not supported"),
	COLUMN_DOESNT_EXIST_FILTER("KER-MSD-348","Received column does not support filter"),
	INVALID_COLUMN_NAME("KER-MSD-325","Invalid Column Name passed");

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
