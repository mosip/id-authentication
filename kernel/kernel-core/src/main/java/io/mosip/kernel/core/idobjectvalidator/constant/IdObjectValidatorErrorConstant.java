package io.mosip.kernel.core.idobjectvalidator.constant;

/**
 * Enum containing custom error codes and the respective messages.
 * 
 * @author Manoj SP
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public enum IdObjectValidatorErrorConstant {

	SCHEMA_IO_EXCEPTION("KER-IOV-001", "Failed to read schema"),
	
	ID_OBJECT_VALIDATION_FAILED("KER-IOV-002", "Id Object validation failed"),
	
	ID_OBJECT_PARSING_FAILED("KER-IOV-003", "Failed to parse/convert Id Object"),
	
	INVALID_INPUT_PARAMETER("KER-IOV-004", "Invalid Input Parameter - %s"),
	
	MISSING_INPUT_PARAMETER("KER-IOV-005", "Missing Input Parameter - %s"),
	
	MASTERDATA_LOAD_FAILED("KER-IOV-006", "Failed to load data from kernel masterdata"),
	
	MANDATORY_FIELDS_NOT_FOUND("KER-IOV-007", "Mandatory fields are not found for %s operation");

	private final String errorCode;

	private final String message;

	/**
	 * Instantiates a new json validator error constant.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 */
	IdObjectValidatorErrorConstant(final String errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
