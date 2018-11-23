package io.mosip.kernel.masterdata.constant;

import io.mosip.kernel.masterdata.service.impl.IdTypeServiceImpl;

/**
 * Error Code and Messages ENUM for {@link IdTypeServiceImpl}
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum IdTypeErrorCode {
	ID_TYPE_FETCH_EXCEPTION("KER-MSD-021", "Error occured while fetching Id Types"), ID_TYPE_INSERT_EXCEPTION(
			"KER-MSD-444",
			"Exception during inserting data into db"), ID_TYPE_NOT_FOUND("KER-MSD-022", "Id Type found");

	/**
	 * The error code.
	 */
	private final String errorCode;

	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * Constructor for IdTypeErrorCode.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private IdTypeErrorCode(final String errorCode, final String errorMessage) {
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
