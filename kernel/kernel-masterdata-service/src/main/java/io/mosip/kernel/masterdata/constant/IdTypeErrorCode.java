package io.mosip.kernel.masterdata.constant;

/**
 * ENUM class for handling the constants of IdType.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum IdTypeErrorCode {
	ID_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-022", "ID Type not found");

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
