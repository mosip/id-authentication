package io.mosip.kernel.masterdata.constant;

/**
 * ENUM class for handling the constants of IdType.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum IdTypeErrorCode {
	ID_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-022", "ID Type not found."),
	ID_TYPE_FETCH_EXCEPTION("KER-MSD-021","Error occurred while fetching ID Types"),
	ID_TYPE_INSERT_EXCEPTION("KER-MSD-059","Error occurred while inserting ID Type details.");

	private final String errorCode;	
	private final String errorMessage;

	private IdTypeErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
